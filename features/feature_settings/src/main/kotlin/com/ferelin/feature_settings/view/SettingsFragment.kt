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

package com.ferelin.feature_settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.core.adapter.options.itemDecoration.OptionDecoration
import com.ferelin.core.utils.LoadState
import com.ferelin.core.utils.setOnClick
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.feature_settings.databinding.FragmentSettingsBinding
import com.ferelin.feature_settings.viewModel.SettingsViewModel
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSettingsBinding
        get() = FragmentSettingsBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<SettingsViewModel>

    private val mViewModel: SettingsViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = 200L
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = 200L
        }
    }

    override fun initUi() {
        mViewBinding.recyclerView.apply {
            adapter = mViewModel.optionsAdapter
            addItemDecoration(OptionDecoration(requireContext()))
        }
    }

    override fun initUx() {
        mViewBinding.imageBack.setOnClick(mViewModel::onBackClick)
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch(mDispatchersProvider.IO) {
            observeMenuOptions()
        }
    }

    private suspend fun observeMenuOptions() {
        mViewModel.optionsLoadState.collect { loadState ->
            if (loadState is LoadState.None) {
                mViewModel.loadOptions()
            }
        }
    }

    companion object {

        fun newInstance(data: Any? = null): SettingsFragment {
            return SettingsFragment()
        }
    }
}