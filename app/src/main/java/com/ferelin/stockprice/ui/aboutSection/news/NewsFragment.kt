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

package com.ferelin.stockprice.ui.aboutSection.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentNewsBinding
import com.ferelin.stockprice.ui.aboutSection.news.adapter.NewsClickListener
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.viewModelFactories.CompanyViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsFragment(
    selectedCompany: AdaptiveCompany? = null
) : BaseFragment<FragmentNewsBinding, NewsViewModel, NewsViewController>(), NewsClickListener {

    override val mViewController = NewsViewController()
    override val mViewModel: NewsViewModel by viewModels {
        CompanyViewModelFactory(selectedCompany)
    }

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNewsBinding
        get() = FragmentNewsBinding::inflate

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        setUpClickListeners()
        setUpViewControllerArguments()
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectStateCompanyNews() }
            launch { collectStateOnError() }
        }
    }

    override fun onNewsUrlClicked(position: Int) {
        mViewController.onNewsUrlClicked(mViewModel.selectedCompany, position)
    }

    private fun setUpClickListeners() {
        mViewController.viewBinding.fab.setOnClickListener {
            mViewController.onFabClicked()
        }
    }

    private suspend fun collectStateCompanyNews() {
        mViewModel.stateCompanyNews.collect { notificator ->
            withContext(mCoroutineContext.Main) {
                when (notificator) {
                    is DataNotificator.Loading -> {
                        mViewController.onDataLoadingStateChanged(true)
                    }
                    else -> mViewController.onDataLoadingStateChanged(false)
                }
            }
        }
    }

    private suspend fun collectStateOnError() {
        mViewModel.eventOnError.collect {
            withContext(mCoroutineContext.Main) {
                mViewController.onError()
            }
        }
    }

    private fun setUpViewControllerArguments() {
        mViewModel.newsRecyclerAdapter.setOnNewsClickListener(this)
        mViewController.setArgumentsViewDependsOn(mViewModel.newsRecyclerAdapter)
    }
}