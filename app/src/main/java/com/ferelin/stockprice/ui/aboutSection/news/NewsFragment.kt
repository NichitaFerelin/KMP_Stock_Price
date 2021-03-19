package com.ferelin.stockprice.ui.aboutSection.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentNewsBinding
import com.ferelin.stockprice.ui.aboutSection.newsDetails.NewsDetailsFragment
import com.ferelin.stockprice.utils.showSnackbar
import com.ferelin.stockprice.viewModelFactories.CompanyViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsFragment(
    ownerCompany: AdaptiveCompany? = null
) : BaseFragment<NewsViewModel>(), NewsClickListener {

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
            adapter = mViewModel.recyclerAdapter.also {
                it.setOnNewsClickListener(this@NewsFragment)
            }
            addItemDecoration(NewsItemDecoration(requireContext()))
        }

        mBinding.fab.setOnClickListener {
            mBinding.recyclerViewNews.scrollToPosition(0)
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch {
                mViewModel.actionOpenNewsDetails.collect {
                    requireParentFragment().parentFragmentManager.commit {
                        replace(R.id.fragmentContainer, NewsDetailsFragment.newInstance(it))
                        addToBackStack(null)
                    }
                }
            }
            launch {
                mViewModel.notificationNewItems.collect {
                    withContext(mCoroutineContext.Main) {
                        Toast.makeText(requireContext(), "new item", Toast.LENGTH_LONG).show()
                    }
                }
            }
            launch {
                mViewModel.notificationDataLoaded.collect {
                    withContext(mCoroutineContext.Main) {
                        if (it) {
                            mBinding.progressBar.visibility = View.GONE
                            mBinding.recyclerViewNews.visibility = View.VISIBLE
                        }
                    }
                }
            }
            launch {
                mViewModel.actionOpenUrl.collect {
                    startActivity(it)
                }
            }
            launch {
                mViewModel.actionShowError.collect {
                    withContext(mCoroutineContext.Main) {
                        showSnackbar(mBinding.root, it)
                    }
                }
            }
        }
    }

    override fun onNewsClicked(position: Int) {
        mViewModel.onNewsClicked(position)
    }

    override fun onNewsUrlClicked(position: Int) {
        mViewModel.onNewsUrlClicked(position)
    }
}