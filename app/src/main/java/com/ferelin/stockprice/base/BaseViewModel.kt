package com.ferelin.stockprice.base

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

import androidx.lifecycle.ViewModel
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractorImpl
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * [BaseViewModel] contains all logic for data loading. [mDataInteractor] makes it possible.
 */
abstract class BaseViewModel : ViewModel() {

    /**
     * Application scope that is not tied to activity/fragment lifecycle
     * */
    @Inject
    protected lateinit var mAppScope: CoroutineScope

    // TODO Impl to Interface
    @Inject
    protected lateinit var mDataInteractor: DataInteractorImpl

    @Inject
    protected lateinit var mCoroutineContext: CoroutineContextProvider

    /*
    * Add your observers here.
    * */
    protected abstract fun initObserversBlock()

    /*
    * To avoid calls non-final function in init-block.
    * */
    private var mWasInitialized = false

    fun initObservers() {
        if (!mWasInitialized) {
            initObserversBlock()
            mWasInitialized = true
        }
    }

    fun triggerCreate() {
        // Do nothing. Used to trigger lazy initialization of view model.
    }
}