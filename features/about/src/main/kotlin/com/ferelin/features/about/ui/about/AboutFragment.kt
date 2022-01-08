package com.ferelin.features.about.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.core.ui.view.BaseFragment
import com.ferelin.core.ui.view.launchAndRepeatWithViewLifecycle
import com.ferelin.core.ui.view.setOnClick
import com.ferelin.features.about.R
import com.ferelin.features.about.databinding.FragmentAboutPagerBinding
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

internal class AboutFragment(
  private val aboutParams: AboutParams
) : BaseFragment<FragmentAboutPagerBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAboutPagerBinding
    get() = FragmentAboutPagerBinding::inflate

  @Inject
  lateinit var viewModelFactory: AboutViewModelFactory.Factory
  private val viewModel: AboutViewModel by viewModels {
    viewModelFactory.create(aboutParams)
  }

  private val backPressedCallback by lazy(NONE) {
    object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        if (viewBinding.viewPager.currentItem != 0) {
          viewBinding.viewPager.setCurrentItem(0, true)
        } else {
          this.remove()
          requireActivity().onBackPressed()
        }
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
      .apply { duration = 200L }
    returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
      .apply { duration = 200L }
  }

  override fun initUi() {
    with(viewBinding) {
      textViewCompanyName.text = viewModel.aboutParams.companyName
      textViewCompanyTicker.text = viewModel.aboutParams.companyTicker

      viewPager.apply {
        adapter = AboutViewAdapter(
          params = viewModel.aboutParams,
          fm = childFragmentManager,
          lifecycle = viewLifecycleOwner.lifecycle
        )
        offscreenPageLimit = 3
      }
      tabLayout.attachViewPager(
        viewPager,
        getString(R.string.titleProfile),
        getString(R.string.titleChart),
        getString(R.string.titleNews)
      )
    }
  }

  override fun initUx() {
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      backPressedCallback
    )
    viewBinding.imageViewBack.setOnClick(viewModel::onBackBtnClick)
    viewBinding.imageViewStar.setOnClick(viewModel::onFavouriteIconClick)
  }

  override fun initObservers() {
    with(viewModel) {
      launchAndRepeatWithViewLifecycle {
        isCompanyFavourite
          .flowOn(Dispatchers.Main)
          .onEach(this@AboutFragment::onCompanyFavourite)
          .launchIn(this)
      }
    }
  }

  override fun onDestroyView() {
    viewBinding.tabLayout.detachViewPager()
    super.onDestroyView()
  }

  private fun onCompanyFavourite(isFavourite: Boolean) {
    /*
    * if() else
    * setContentDescription
    * setIcon
    * anim
    * */
  }
}