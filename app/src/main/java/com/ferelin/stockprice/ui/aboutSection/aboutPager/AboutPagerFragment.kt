package com.ferelin.stockprice.ui.aboutSection.aboutPager

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentAboutPagerBinding
import com.ferelin.stockprice.viewModelFactories.CompanyViewModelFactory
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AboutPagerFragment(
    selectedCompany: AdaptiveCompany? = null
) : BaseFragment<AboutPagerViewModel, AboutPagerViewHelper>() {

    override val mViewHelper: AboutPagerViewHelper = AboutPagerViewHelper()
    override val mViewModel: AboutPagerViewModel by viewModels {
        CompanyViewModelFactory(mCoroutineContext, mDataInteractor, selectedCompany)
    }

    private val mSelectedCompany = selectedCompany

    private lateinit var mBinding: FragmentAboutPagerBinding
    private lateinit var mLastSelectedTab: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
        }
        exitTransition = Hold()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAboutPagerBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeTransition(view)
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)

        restoreSelectedTab()
        setUpViewPager()
        setUpBackPressedCallback()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            setUpTabListeners()

            mBinding.imageViewBack.setOnClickListener { activity?.onBackPressed() }
            mBinding.imageViewStar.setOnClickListener {
                mViewModel.onFavouriteIconClicked()
                mViewHelper.runScaleInOut(it)
            }
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch {
                mViewModel.eventDataChanged.collect {
                    withContext(mCoroutineContext.Main) {
                        onDataChanged()
                    }
                }
            }
            launch {
                mViewModel.actionShowError.collect {
                    withContext(mCoroutineContext.Main) {
                        showToast(it)
                    }
                }
            }
        }
    }

    private fun onTabClicked(newTab: TextView, position: Int) {
        if (newTab != mLastSelectedTab) {
            changeTabStyle(mLastSelectedTab, newTab)
            changeTabVariables(newTab, position)
            moveViewPager(position)
        }
    }

    private fun changeTabVariables(newTab: TextView, position: Int) {
        mLastSelectedTab = newTab
        mViewModel.setSelectedTab(position)
    }

    private fun changeTabStyle(previousTab: TextView, newTab: TextView) {
        TextViewCompat.setTextAppearance(previousTab, R.style.textViewBodyShadowedTab)
        TextViewCompat.setTextAppearance(newTab, R.style.textViewH3Tab)
        mViewHelper.runScaleInOut(newTab)
    }

    private fun moveViewPager(position: Int) {
        mBinding.viewPager.setCurrentItem(position, true)
    }

    private fun restoreSelectedTab() {
        mLastSelectedTab = getTextViewTabByPosition(mViewModel.lastSelectedPage)
        if (mBinding.viewPager.currentItem == 0 && mLastSelectedTab != mBinding.textViewChart) {
            changeTabStyle(mBinding.textViewChart, mLastSelectedTab)
        }
    }

    private fun getTextViewTabByPosition(position: Int): TextView {
        return when (position) {
            0 -> mBinding.textViewChart
            1 -> mBinding.textViewSummary
            2 -> mBinding.textViewNews
            3 -> mBinding.textViewForecasts
            4 -> mBinding.textViewIdeas
            else -> throw IllegalStateException("Unchecked tab position: ${mViewModel.lastSelectedPage}")
        }
    }

    private fun onDataChanged() {
        mBinding.apply {
            textViewCompanyName.text = mViewModel.companyName
            textViewCompanySymbol.text = mViewModel.companySymbol
            mBinding.imageViewStar.setImageResource(mViewModel.companyFavouriteIconResource)
        }
    }

    private fun postponeTransition(view: View) {
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun setUpTabListeners() {
        mBinding.apply {
            textViewChart.setOnClickListener { onTabClicked(it as TextView, 0) }
            textViewSummary.setOnClickListener { onTabClicked(it as TextView, 1) }
            textViewNews.setOnClickListener { onTabClicked(it as TextView, 2) }
            textViewForecasts.setOnClickListener { onTabClicked(it as TextView, 3) }
            textViewIdeas.setOnClickListener { onTabClicked(it as TextView, 4) }
        }
    }

    private fun setUpViewPager() {
        mBinding.viewPager.adapter =
            AboutPagerAdapter(childFragmentManager, lifecycle, mSelectedCompany)
        mBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val newTextView = getTextViewTabByPosition(position)
                onTabClicked(newTextView, position)
            }
        })
    }

    private fun setUpBackPressedCallback() {
        activity?.onBackPressedDispatcher?.addCallback(
            this@AboutPagerFragment,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (this@AboutPagerFragment::mBinding.isInitialized && mBinding.viewPager.currentItem != 0) {
                        mBinding.viewPager.setCurrentItem(0, true)
                    } else {
                        this.remove()
                        activity?.onBackPressed()
                    }
                }
            })
    }
}