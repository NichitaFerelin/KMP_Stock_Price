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

package com.ferelin.stockprice.ui.aboutSection.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.databinding.FragmentProfileBinding
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.utils.showToast
import com.ferelin.stockprice.viewModelFactories.CompanyViewModelFactory


class ProfileFragment(private val mSelectedCompany: AdaptiveCompany? = null) : Fragment() {

    private var mBinding: FragmentProfileBinding? = null

    private val mViewModel: ProfileViewModel by viewModels {
        CompanyViewModelFactory(mSelectedCompany)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImage()
        initFields()
        setUpListeners()
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun initImage() {
        Glide
            .with(mBinding!!.root)
            .load(mViewModel.logoUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(mBinding!!.imageViewIcon)
    }

    private fun initFields() {
        with(mBinding!!) {
            textViewName.text = mViewModel.companyName
            textViewWebUrl.text = mViewModel.companyWebUrl
            textViewCountry.text = mViewModel.country
            textViewIndustry.text = mViewModel.industry
            textViewPhone.text = mViewModel.phone
            textViewCapitalization.text = mViewModel.capitalization
        }
    }

    private fun setUpListeners() {
        mBinding!!.textViewWebUrl.setOnClickListener {
            val url = mViewModel.companyWebUrl
            val isNavigated = Navigator.navigateToUrl(requireContext(), url)
            if (!isNavigated) {
                showNoAppError()
            }
        }

        mBinding!!.textViewPhone.setOnClickListener {
            val isNavigated = Navigator.navigateToContacts(requireContext(), mViewModel.phone)
            if (!isNavigated) {
                showNoAppError()
            }
        }
    }

    private fun showNoAppError() {
        showToast(requireContext(), getString(R.string.errorNoAppToOpenUrl))
    }
}