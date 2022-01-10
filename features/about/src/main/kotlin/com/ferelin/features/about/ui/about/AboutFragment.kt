package com.ferelin.features.about.ui.about

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.core.ui.view.BaseFragment
import com.ferelin.core.ui.view.launchAndRepeatWithViewLifecycle
import com.ferelin.core.ui.view.setOnClick
import com.ferelin.features.about.R
import com.ferelin.features.about.databinding.FragmentAboutPagerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

internal class AboutFragment : BaseFragment<FragmentAboutPagerBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAboutPagerBinding
    get() = FragmentAboutPagerBinding::inflate

  @Inject
  lateinit var viewModelFactory: AboutViewModelFactory.Factory
  private val viewModel: AboutViewModel by viewModels {
    val params = requireArguments()[ABOUT_SCREEN_KEY] as AboutParams
    viewModelFactory.create(params)
  }

  private val backPressedCallback by lazy(NONE) {
    object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        if (viewBinding.viewPager.currentItem != 0) {
          viewBinding.viewPager.setCurrentItem(0, true)
        } else {
          this.remove()
          viewModel.onBackBtnClick()
        }
      }
    }
  }

  override fun onAttach(context: Context) {
    ViewModelProvider(this).get<AboutComponentViewModel>()
      .aboutComponent
      .inject(this)
    super.onAttach(context)
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
    viewBinding.imageViewStar.setImageResource(
      if (isFavourite) R.drawable.ic_favourite_active_16 else R.drawable.ic_favourite_16
    )
  }
}