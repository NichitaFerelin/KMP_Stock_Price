package com.ferelin.stockprice.ui.aboutSection.aboutPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentAboutPagerBinding
import com.ferelin.stockprice.viewModelFactories.CompanyViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AboutPagerFragment(
    ownerCompany: AdaptiveCompany? = null
) : BaseFragment<AboutPagerViewModel>() {

    private lateinit var mBinding: FragmentAboutPagerBinding
    private val mOwnerCompany = ownerCompany

    private lateinit var mLastSelectedTab: TextView

    override val mViewModel: AboutPagerViewModel by viewModels {
        CompanyViewModelFactory(mCoroutineContext, mDataInteractor, ownerCompany)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAboutPagerBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun setUpViewComponents() {
        initSelectedTab()

        mBinding.apply {

            viewPager.apply {
                adapter = AboutPagerAdapter(childFragmentManager, lifecycle, mOwnerCompany)
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        val newTextView = getTextViewTabByPosition(position)
                        onTabClicked(newTextView, position)
                    }
                })

                // Tab state restore
                if (currentItem == 0 && mLastSelectedTab != textViewChart) {
                    changeTabStyle(textViewChart, mLastSelectedTab)
                }
            }

            textViewChart.setOnClickListener { onTabClicked(it as TextView, 0) }
            textViewSummary.setOnClickListener { onTabClicked(it as TextView, 1) }
            textViewNews.setOnClickListener { onTabClicked(it as TextView, 2) }
            textViewForecasts.setOnClickListener { onTabClicked(it as TextView, 3) }
            textViewIdeas.setOnClickListener { onTabClicked(it as TextView, 4) }

            imageViewStar.setOnClickListener {
                mViewModel.onFavouriteIconClicked()
            }
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mViewModel.eventDataChanged.collect {
                withContext(mCoroutineContext.Main) {
                    applyDataChanges()
                }
            }
        }
    }

    private fun onTabClicked(new: TextView, position: Int) {
        if (new != mLastSelectedTab) {
            changeTabStyle(mLastSelectedTab, new)
            changeTabVariables(new, position)
            moveViewPager(position)
        }
    }

    private fun changeTabVariables(new: TextView, position: Int) {
        mLastSelectedTab = new
        mViewModel.setSelectedTab(position)
    }

    private fun changeTabStyle(lastTab: TextView, new: TextView) {
        TextViewCompat.setTextAppearance(lastTab, R.style.textViewBodyShadowed)
        TextViewCompat.setTextAppearance(new, R.style.textViewH3)
    }

    private fun moveViewPager(position: Int) {
        mBinding.viewPager.setCurrentItem(position, true)
    }

    private fun initSelectedTab() {
        mLastSelectedTab = getTextViewTabByPosition(mViewModel.lastSelectedPage)
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

    private fun applyDataChanges() {
        mBinding.apply {
            textViewCompanyName.text = mViewModel.companyName
            textViewCompanySymbol.text = mViewModel.companySymbol
            mBinding.imageViewStar.setImageResource(mViewModel.companyFavouriteIconResource)
        }
    }
}