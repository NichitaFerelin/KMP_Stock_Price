package com.ferelin.features.about.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.params.ProfileParams
import com.ferelin.core.ui.view.BaseFragment
import com.ferelin.core.ui.view.launchAndRepeatWithViewLifecycle
import com.ferelin.core.ui.view.setOnClick
import com.ferelin.features.about.R
import com.ferelin.features.about.databinding.FragmentProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class ProfileFragment(
  params: ProfileParams
) : BaseFragment<FragmentProfileBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
    get() = FragmentProfileBinding::inflate

  @Inject
  lateinit var viewModelFactory: ProfileViewModelFactory.Factory
  private val viewModel: ProfileViewModel by viewModels {
    viewModelFactory.create(params)
  }

  override fun initUx() {
    with(viewBinding) {
      textViewWebUrl.setOnClick(this@ProfileFragment::onWebUrlClick)
      textViewPhone.setOnClick(this@ProfileFragment::onPhoneClick)
      imageViewShare.setOnClick(this@ProfileFragment::onShareClick)
    }
  }

  override fun initObservers() {
    with(viewModel) {
      launchAndRepeatWithViewLifecycle {
        profile
          .flowOn(Dispatchers.Main)
          .onEach(this@ProfileFragment::onProfile)
          .launchIn(this)

        profileLce
          .flowOn(Dispatchers.Main)
          .onEach(this@ProfileFragment::onProfileLce)
          .launchIn(this)
      }
    }
  }

  private fun onProfile(profileViewData: ProfileViewData) {
    with(viewBinding) {
      textViewWebUrl.text = profileViewData.webUrl
      textViewCountry.text = profileViewData.country
      textViewIndustry.text = profileViewData.industry
      textViewPhone.text = profileViewData.phone
      textViewCapitalization.text = profileViewData.capitalization
      viewBinding.textViewName.text = profileViewData.companyName

      Glide
        .with(viewBinding.root)
        .load(profileViewData.logoUrl)
        .transition(DrawableTransitionOptions.withCrossFade())
        .error(
          AppCompatResources.getDrawable(requireContext(), R.drawable.ic_load_error_20)
        )
        .into(viewBinding.imageViewIcon)
    }
  }

  private fun onProfileLce(lceState: LceState) {
    // show progress bar
  }

  private fun onShareClick() {
    // share
  }

  private fun onWebUrlClick() {
    // open url
  }

  private fun onPhoneClick() {
    // open contacts
  }
}