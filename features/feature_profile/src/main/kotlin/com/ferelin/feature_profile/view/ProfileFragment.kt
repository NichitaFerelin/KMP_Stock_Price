/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.feature_profile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.core.params.ProfileParams
import com.ferelin.core.utils.launchAndRepeatWithViewLifecycle
import com.ferelin.core.utils.setOnClick
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.feature_profile.R
import com.ferelin.feature_profile.databinding.FragmentProfileBinding
import com.ferelin.feature_profile.viewData.ProfileViewData
import com.ferelin.feature_profile.viewModel.ProfileViewModel
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<ProfileViewModel>

    private val viewModel: ProfileViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { unpackArgs(it) }
    }

    override fun initUi() {
        viewBinding.textViewName.text = viewModel.profileParams.companyName

        Glide
            .with(viewBinding.root)
            .load(viewModel.profileParams.companyLogoUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_load_error
                )
            )
            .into(viewBinding.imageViewIcon)
    }

    override fun initUx() {
        viewBinding.textViewWebUrl.setOnClick(this::onWebUrlClick)
        viewBinding.textViewPhone.setOnClick(this::onPhoneClick)
        viewBinding.imageViewShare.setOnClick(this::onShareClick)
    }

    override fun initObservers() {
        launchAndRepeatWithViewLifecycle {
            observeProfileState()
        }
    }

    private suspend fun observeProfileState() {
        viewModel.profileLoadState.collect { loadState ->
            loadState.ifPrepared {
                withContext(Dispatchers.Main) {
                    setProfile(it.data)
                }
            }
        }
    }

    private fun onShareClick() {
        viewModel.onShareClick(
            nameHint = getString(R.string.hintName),
            websiteHint = getString(R.string.hintWebsite),
            countryHint = getString(R.string.hintCountry),
            industryHint = getString(R.string.hintIndustry),
            phoneHint = getString(R.string.hintPhone),
            capitalizationHint = getString(R.string.hintCapitalization)
        )
    }

    private fun onWebUrlClick() {
        val processed = viewModel.onUrlClick()
        if (!processed) {
            showTempSnackbar(getString(R.string.errorNoAppToResolve))
        }
    }

    private fun onPhoneClick() {
        val phoneNumber = viewBinding.textViewPhone.text.toString()
        val processed = viewModel.onPhoneClick(phoneNumber)
        if (!processed) {
            showTempSnackbar(getString(R.string.errorNoAppToResolve))
        }
    }

    private fun setProfile(profileViewData: ProfileViewData) {
        with(viewBinding) {
            TransitionManager.beginDelayedTransition(viewBinding.root)
            textViewWebUrl.text = profileViewData.webUrl
            textViewCountry.text = profileViewData.country
            textViewIndustry.text = profileViewData.industry
            textViewPhone.text = profileViewData.phone
            textViewCapitalization.text = profileViewData.capitalization
        }
    }

    private fun unpackArgs(args: Bundle) {
        args[profileParamsKey]?.let { params ->
            if (params is ProfileParams) {
                viewModel.profileParams = params
            }
        }
    }

    companion object {

        private const val profileParamsKey = "p"

        fun newInstance(data: Any?): ProfileFragment {
            return ProfileFragment().also {
                if (data is ProfileParams) {
                    it.arguments = bundleOf(profileParamsKey to data)
                }
            }
        }
    }
}