package com.ferelin.stockprice.services.observer

/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [StockObserverController] provides methods to control [StockObserverService]
 * */
@Singleton
class StockObserverController @Inject constructor(
    private val mAppScope: CoroutineScope,
    private val mDataInteractor: DataInteractor,
    private val mContext: Context
) {
    private var mCompanyCollectorJob: Job? = null
    private var mCompanyObserverJob: Job? = null

    private var mIsServiceRunning: Boolean = false

    private var mCompanyToObserve: AdaptiveCompany? = null

    fun onActivityResume(context: Context) {
        if (mIsServiceRunning) {
            mCompanyCollectorJob?.cancel()
            mCompanyCollectorJob = null
            stopService(context)
        }
    }

    fun onActivityNotFinishingPause() {
        collectMessagesForService()
    }

    fun onActivityFinishingDestroy(context: Context) {
        stopService(context)
    }

    private fun onTargetCompanyChanged(newTarget: AdaptiveCompany) {
        mCompanyToObserve = newTarget
        updateService(mContext, newTarget)
        mIsServiceRunning = true
        collectCompanyUpdates()
    }

    private fun collectCompanyUpdates() {
        mCompanyObserverJob?.cancel()
        mCompanyObserverJob = mAppScope.launch {
            mDataInteractor.sharedCompaniesUpdates
                .filter { filterSharedCompanyUpdate(it) }
                .collect { notificator -> updateService(mContext, notificator.data!!) }
        }
    }

    private fun collectMessagesForService() {
        mAppScope.launch {
            mCompanyCollectorJob = mAppScope.launch {
                mDataInteractor.stateCompanyForObserver.collect { companyToObserve ->
                    if (!isActive) {
                        return@collect
                    }

                    if (companyToObserve == null) {
                        mIsServiceRunning = false
                        stopService(mContext)
                    } else onTargetCompanyChanged(companyToObserve)
                }
            }
        }
    }

    private fun updateService(context: Context, company: AdaptiveCompany) {
        val serviceIntent = Intent(context, StockObserverService::class.java).apply {
            putExtra(StockObserverService.KEY_COMPANY_NAME_STR, company.companyProfile.name)
            putExtra(StockObserverService.KEY_PRICE_STR, company.companyDayData.currentPrice)
            putExtra(StockObserverService.KEY_PROFIT_STR, company.companyDayData.profit)
            putExtra(
                StockObserverService.KEY_PROFIT_TEXT_COLOR_RES,
                company.companyStyle.dayProfitBackground
            )
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    private fun stopService(context: Context) {
        val serviceIntent = Intent(context, StockObserverService::class.java).apply {
            putExtra(StockObserverService.KEY_STOP, StockObserverService.KEY_STOP)
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    private fun filterSharedCompanyUpdate(notificator: DataNotificator<AdaptiveCompany>): Boolean {
        return mCompanyToObserve != null
                && notificator is DataNotificator.ItemUpdatedCommon
                && notificator.data != null
                && notificator.data == mCompanyToObserve
    }
}