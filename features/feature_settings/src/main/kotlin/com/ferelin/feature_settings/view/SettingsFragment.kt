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

import android.Manifest
import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.ferelin.core.adapter.options.itemDecoration.OptionDecoration
import com.ferelin.core.resolvers.PermissionsResolver
import com.ferelin.core.utils.launchAndRepeatWithViewLifecycle
import com.ferelin.core.utils.setOnClick
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.feature_settings.R
import com.ferelin.feature_settings.databinding.FragmentSettingsBinding
import com.ferelin.feature_settings.viewModel.SettingsEvent
import com.ferelin.feature_settings.viewModel.SettingsViewModel
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSettingsBinding
        get() = FragmentSettingsBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<SettingsViewModel>

    private val viewModel: SettingsViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private val requestPathLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { resultUri: Uri? ->
        viewModel.onPathSelected(resultUri)
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.onPermissionsGranted()
        } else {
            showTempSnackbar(getString(R.string.messagePermissionsNotGranted))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            .apply { duration = 200L }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            .apply { duration = 200L }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            .apply { duration = 200L }
    }

    override fun initUi() {
        viewBinding.recyclerView.apply {
            adapter = viewModel.optionsAdapter
            addItemDecoration(OptionDecoration(requireContext()))
        }
    }

    override fun initUx() {
        viewBinding.imageBack.setOnClick(viewModel::onBackClick)
    }

    override fun initObservers() {
        launchAndRepeatWithViewLifecycle {
            viewModel.loadOptions()
            observeMessageEvent()
        }
    }

    override fun onDestroyView() {
        viewBinding.recyclerView.adapter = null
        super.onDestroyView()
    }

    private suspend fun observeMessageEvent() {
        viewModel.messageSettingsEvent.collect { event ->
            withContext(Dispatchers.Main) {
                when (event) {
                    SettingsEvent.LOG_OUT_COMPLETE -> {
                        showTempSnackbar(getString(R.string.messageLogOutComplete))
                    }
                    SettingsEvent.DATA_CLEARED_NO_NETWORK -> {
                        showTempSnackbar(getString(R.string.messageDataClearedNoNetwork))
                    }
                    SettingsEvent.DATA_CLEARED -> {
                        showTempSnackbar(getString(R.string.messageDataCleared))
                    }
                    SettingsEvent.REQUEST_PATH -> {
                        requestPath()
                    }
                    SettingsEvent.REQUEST_PERMISSIONS -> {
                        requestPermissions()
                    }
                    SettingsEvent.DOWNLOAD_PATH_ERROR -> {
                        showActionSnackbar(
                            getString(R.string.errorPath),
                            getString(R.string.hintChoose)
                        ) {
                            requestPermissions()
                        }
                    }
                    SettingsEvent.DOWNLOAD_STARTING -> {
                        showTempSnackbar(getString(R.string.messageDownloadingWillStart))
                    }
                    SettingsEvent.DOWNLOAD_WILL_BE_STARTED -> {
                        showTempSnackbar(getString(R.string.messageDownloadingNoNetwork))
                    }
                    SettingsEvent.DOWNLOAD_ERROR -> {
                        showTempSnackbar(getString(R.string.errorUndefined))
                    }
                }
            }
        }
    }

    private fun requestPermissions() {
        val hasPermissions = PermissionsResolver.writeExternalStorage(requireContext())
        if (!hasPermissions) {
            requestPermissionsLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            viewModel.onPermissionsGranted()
        }
    }

    private fun requestPath() {
        try {
            requestPathLauncher.launch(Uri.EMPTY)
        } catch (exception: ActivityNotFoundException) {
            showTempSnackbar(getString(R.string.errorNoAppToResolve))
        }
    }

    companion object {
        fun newInstance(data: Any? = null): SettingsFragment {
            return SettingsFragment()
        }
    }
}