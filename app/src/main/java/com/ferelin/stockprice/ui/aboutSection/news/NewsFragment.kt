package com.ferelin.stockprice.ui.aboutSection.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
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
        postponeEnterTransition()
        view?.doOnPreDraw { startPostponedEnterTransition() }

        mBinding.recyclerViewNews.apply {
            adapter = mViewModel.recyclerAdapter.also {
                it.setOnNewsClickListener(this@NewsFragment)
            }
            addItemDecoration(NewsItemDecoration(requireContext()))
        }

        mBinding.fab.setOnClickListener {
            if((mBinding.recyclerViewNews.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 12) {
                val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_out)
                val animEnd =
                    AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in)
                animEnd.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        mBinding.recyclerViewNews.visibility = View.VISIBLE
                        mBinding.recyclerViewNews.smoothScrollToPosition(0)

                        val anim2 = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_out_2)
                        mBinding.fab.startAnimation(anim2)
                        anim2.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation?) {

                            }

                            override fun onAnimationEnd(animation: Animation?) {
                                mBinding.fab.visibility = View.INVISIBLE
                                mBinding.fab.scaleX = 1.0F
                                mBinding.fab.scaleY = 1.0F
                            }

                            override fun onAnimationRepeat(animation: Animation?) {
                            }
                        })
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                })


                mBinding.recyclerViewNews.startAnimation(anim)
                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {
                        mBinding.recyclerViewNews.visibility = View.GONE
                        mBinding.recyclerViewNews.scrollToPosition(7)
                        mBinding.recyclerViewNews.startAnimation(animEnd)
                        //mBinding.recyclerViewNews.y += mBinding.recyclerViewNews.height
                        //mBinding.recyclerViewNews.startAnimation(animEnd)
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationStart(animation: Animation?) {
                        mBinding.recyclerViewNews.smoothScrollBy(
                            0,
                            (-mBinding.recyclerViewNews.height).toInt()
                        )
                    }
                })
                /*mBinding.recyclerViewNews.smoothScrollBy(
                    0,
                    (-mBinding.recyclerViewNews.height / 1.2).toInt()
                )*/

                //
            } else {
                mBinding.recyclerViewNews.smoothScrollToPosition(0)
            }
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch {
                mViewModel.actionOpenNewsDetails.collect {

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
                mViewModel.hasDataForRecycler.collect { hasData ->
                    Log.d("Test", "Collect: $hasData")
                    withContext(mCoroutineContext.Main) {
                        if (hasData && mBinding.recyclerViewNews.visibility == View.GONE) {
                            Log.d("Test", "Set visible")
                            TransitionManager.beginDelayedTransition(mBinding.root)
                            mBinding.recyclerViewNews.visibility = View.VISIBLE
                        } else if (!hasData && mBinding.recyclerViewNews.visibility == View.VISIBLE) {
                            Log.d("Test", "set hide")
                            mBinding.recyclerViewNews.visibility = View.GONE
                            mBinding.progressBar.visibility = View.VISIBLE
                        }

                        if (hasData) {
                            mBinding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }

           /* launch {
                mViewModel.notificationDataLoaded.collect {
                    withContext(mCoroutineContext.Main) {
                        if (it) {
                            mBinding.progressBar.visibility = View.GONE
                            mBinding.recyclerViewNews.visibility = View.VISIBLE
                        }
                    }
                }
            }*/
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

    override fun onNewsClicked(
        holder: NewsRecyclerAdapter.NewsViewHolder,
        source: String,
        headline: String,
        summary: String,
        date: String,
        url: String
    ) {
        requireParentFragment().parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.fragmentContainer,
                NewsDetailsFragment.newInstance(
                    source,
                    headline,
                    summary,
                    date,
                    url,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )
            addToBackStack(null)
            addSharedElement(holder.binding.root, "rootTransition")
        }
    }

    override fun onNewsUrlClicked(position: Int) {
        mViewModel.onNewsUrlClicked(position)
    }
}