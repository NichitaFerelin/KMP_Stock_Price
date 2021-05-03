package com.ferelin.stockprice.ui.aboutSection.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentNewsBinding
import com.ferelin.stockprice.viewModelFactories.CompanyViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsFragment(
    selectedCompany: AdaptiveCompany? = null
) : BaseFragment<FragmentNewsBinding, NewsViewModel, NewsViewController>(), NewsClickListener {

    override val mViewController: NewsViewController = NewsViewController()
    override val mViewModel: NewsViewModel by viewModels {
        CompanyViewModelFactory(mCoroutineContext, mDataInteractor, selectedCompany)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewBinding = FragmentNewsBinding.inflate(inflater, container, false)
        mViewController.viewBinding = viewBinding
        return viewBinding.root
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        setUpClickListeners()
        setUpViewControllerArguments()
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectStateNews() }
            launch { collectStateIsDataLoading() }
            launch { collectStateOnError() }
        }
    }

    override fun onNewsUrlClicked(position: Int) {
        mViewController.onNewsUrlClicked(mViewModel.selectedCompany, position)
    }

    private fun setUpClickListeners() {
        mViewController.viewBinding!!.fab.setOnClickListener {
            mViewController.onFabClicked()
        }
    }

    private suspend fun collectStateNews() {
        mViewModel.stateNews
            .filter { it != null }
            .collect { news ->
                withContext(mCoroutineContext.Main) {
                    mViewController.onNewsChanged(news!!)
                }
            }
    }

    private suspend fun collectStateIsDataLoading() {
        mViewModel.stateIsDataLoading.collect { isDataLoading ->
            withContext(mCoroutineContext.Main) {
                mViewController.onDataLoadingStateChanged(isDataLoading)
            }
        }
    }

    private suspend fun collectStateOnError() {
        mViewModel.eventOnError.collect { message ->
            withContext(mCoroutineContext.Main) {
                mViewController.onError(message)
            }
        }
    }

    private fun setUpViewControllerArguments() {
        mViewModel.newsRecyclerAdapter.setOnNewsClickListener(this)
        mViewController.setArgumentsViewDependsOn(
            newsAdapter = mViewModel.newsRecyclerAdapter
        )
    }
}