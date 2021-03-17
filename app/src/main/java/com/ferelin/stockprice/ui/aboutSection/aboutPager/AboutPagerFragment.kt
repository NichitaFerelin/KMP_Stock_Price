package com.ferelin.stockprice.ui.aboutSection.aboutPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
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
        mBinding.apply {
            viewPager.adapter = AboutPagerAdapter(childFragmentManager, lifecycle, mOwnerCompany)
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

    private fun applyDataChanges() {
        mBinding.apply {
            textViewCompanyName.text = mViewModel.companyName
            textViewCompanySymbol.text = mViewModel.companySymbol
            mBinding.imageViewStar.setImageResource(mViewModel.companyFavouriteIconResource)
        }
    }
}