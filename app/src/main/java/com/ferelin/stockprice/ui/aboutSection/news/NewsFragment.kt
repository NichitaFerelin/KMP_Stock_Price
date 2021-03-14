package com.ferelin.stockprice.ui.aboutSection.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentNewsBinding
import com.ferelin.stockprice.ui.aboutSection.newsDetails.NewsDetailsFragment
import com.ferelin.stockprice.viewModelFactories.ArgsViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class NewsFragment : BaseFragment<NewsViewModel>() {

    private lateinit var mBinding: FragmentNewsBinding

    override val mViewModel: NewsViewModel by viewModels {
        ArgsViewModelFactory(mCoroutineContext, mDataInteractor, arguments)
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
        mBinding.recyclerViewNews.adapter = mViewModel.recyclerAdapter
    }

    override fun initObservers() {
        super.initObservers()

        lifecycleScope.launch(mCoroutineContext.IO) {

            mViewModel.eventOpenNewsDetails
                .take(1)
                .collect {
                    requireParentFragment().parentFragmentManager.commit {
                        replace(R.id.fragmentContainer, NewsDetailsFragment.newInstance(it))
                        addToBackStack(null)
                    }
                }
        }
    }

    companion object {
        const val KEY_IMAGE_URL = "image_url"
        const val KEY_HEADLINE = "headline"
        const val KEY_SUMMARY = "summary"
        const val KEY_BROWSER_URL = "url"
        const val KEY_DATE = "date"

        fun newInstance(args: Bundle?): NewsFragment {
            return NewsFragment().apply {
                arguments = args
            }
        }
    }
}