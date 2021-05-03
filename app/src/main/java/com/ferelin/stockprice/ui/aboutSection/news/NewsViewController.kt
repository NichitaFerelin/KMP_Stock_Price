package com.ferelin.stockprice.ui.aboutSection.news

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyNews
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.common.ViewAnimatorScrollable
import com.ferelin.stockprice.databinding.FragmentNewsBinding
import com.ferelin.stockprice.utils.anim.AnimationManager
import com.ferelin.stockprice.utils.showToast

class NewsViewController : BaseViewController<ViewAnimatorScrollable, FragmentNewsBinding>() {

    override val mViewAnimator = ViewAnimatorScrollable()

    override fun onViewCreated(
        savedInstanceState: Bundle?,
        fragment: Fragment,
        viewLifecycleScope: LifecycleCoroutineScope
    ) {
        super.onViewCreated(savedInstanceState, fragment, viewLifecycleScope)
        setUpRecyclerView()
    }

    override fun onDestroyView() {
        postponeReferencesRemove {
            viewBinding!!.recyclerViewNews.adapter = null
            super.onDestroyView()
        }
    }

    fun setArgumentsViewDependsOn(newsAdapter: NewsRecyclerAdapter) {
        viewBinding!!.recyclerViewNews.adapter = newsAdapter
    }

    fun onNewsChanged(news: AdaptiveCompanyNews) {
        (viewBinding!!.recyclerViewNews.adapter as NewsRecyclerAdapter).setData(news)
    }

    /*
    * Start intent to open URL
    * */
    fun onNewsUrlClicked(company: AdaptiveCompany, position: Int) {
        val url = company.companyNews.browserUrls[position]
        val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
        try {
            mContext!!.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                mContext!!,
                R.string.errorNoAppToOpenUrl,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun onDataLoadingStateChanged(isDataLoading: Boolean) {
        if (isDataLoading) {
            viewBinding!!.progressBar.visibility = View.VISIBLE
        } else viewBinding!!.progressBar.visibility = View.GONE
    }

    fun onFabClicked() {
        scrollToTop()
    }

    fun onError(message: String) {
        mContext?.let { showToast(it, message) }
        viewBinding!!.progressBar.visibility = View.INVISIBLE
    }

    private fun setUpRecyclerView() {
        viewBinding!!.recyclerViewNews.addItemDecoration(NewsItemDecoration(viewBinding!!.root.context))
    }

    private fun scrollToTop() {
        val fabScaleOutCallback = object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                viewBinding!!.fab.visibility = View.INVISIBLE
                viewBinding!!.fab.scaleX = 1.0F
                viewBinding!!.fab.scaleY = 1.0F
            }
        }
        if ((viewBinding!!.recyclerViewNews.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 12) {
            val fadeInCallback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    viewBinding!!.recyclerViewNews.visibility = View.VISIBLE
                    viewBinding!!.recyclerViewNews.smoothScrollToPosition(0)
                    mViewAnimator.runScaleOutAnimation(viewBinding!!.fab, fabScaleOutCallback)
                }
            }
            val fadeOutCallback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    viewBinding!!.recyclerViewNews.smoothScrollBy(
                        0,
                        -viewBinding!!.recyclerViewNews.height
                    )
                }

                override fun onAnimationEnd(animation: Animation?) {
                    viewBinding!!.recyclerViewNews.visibility = View.GONE
                    viewBinding!!.recyclerViewNews.scrollToPosition(7)
                    mViewAnimator.runFadeInAnimation(viewBinding!!.recyclerViewNews, fadeInCallback)
                }
            }
            mViewAnimator.runFadeOutAnimation(viewBinding!!.recyclerViewNews, fadeOutCallback)
        } else {
            mViewAnimator.runScaleOutAnimation(viewBinding!!.fab, fabScaleOutCallback)
            viewBinding!!.recyclerViewNews.smoothScrollToPosition(0)
        }
    }
}