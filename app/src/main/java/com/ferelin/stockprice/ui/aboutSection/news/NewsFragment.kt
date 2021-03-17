package com.ferelin.stockprice.ui.aboutSection.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentNewsBinding
import com.ferelin.stockprice.ui.aboutSection.newsDetails.NewsDetailsFragment
import com.ferelin.stockprice.viewModelFactories.CompanyViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewsFragment(ownerCompany: AdaptiveCompany? = null) : BaseFragment<NewsViewModel>() {

    private lateinit var mBinding: FragmentNewsBinding

    override val mViewModel: NewsViewModel by viewModels {
        CompanyViewModelFactory(mCoroutineContext, mDataInteractor, ownerCompany)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentNewsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun setUpViewComponents() {
        mBinding.recyclerViewNews.apply {
            adapter = mViewModel.recyclerAdapter
            addItemDecoration(NewsItemDecoration(requireContext()))
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mViewModel.eventOpenNewsDetails.collect {
                requireParentFragment().parentFragmentManager.commit {
                    replace(R.id.fragmentContainer, NewsDetailsFragment.newInstance(it))
                    addToBackStack(null)
                }
            }
        }
    }
}