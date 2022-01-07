package com.ferelin.features.settings.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.ferelin.core.domain.entities.entity.LceState
import com.ferelin.core.ui.view.BaseFragment
import com.ferelin.core.ui.view.launchAndRepeatWithViewLifecycle
import com.ferelin.core.ui.view.setOnClick
import com.ferelin.core.ui.viewModel.BaseViewModelFactory
import com.ferelin.features.settings.ui.databinding.FragmentSettingsBinding
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
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
    if (resultUri != null) {
      viewModel.onStoragePathSelected(
        resultUri.path ?: return@registerForActivityResult,
        resultUri.authority ?: return@registerForActivityResult
      )
    }
  }

  private val requestPermissionsLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
    if (!isGranted) {
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
    viewBinding.imageBack.setOnClick(viewModel::onBack)
  }

  override fun initObservers() {
    with(viewModel) {
      launchAndRepeatWithViewLifecycle {
        event
          .flowOn(Dispatchers.Main)
          .onEach(this@SettingsFragment::onEvent)
          .launchIn(this)

        notifyPriceState
          .flowOn(Dispatchers.Main)
          .onEach(this@SettingsFragment::onNotifyPriceState)
          .launchIn(this)

        downloadLce
          .flowOn(Dispatchers.Main)
          .onEach(this@SettingsFragment::onDownloadLce)
          .launchIn(this)

        authState
          .flowOn(Dispatchers.Main)
          .onEach(this@SettingsFragment::onAuthState)
          .launchIn(this)
      }
    }
  }

  override fun onDestroyView() {
    viewBinding.recyclerView.adapter = null
    super.onDestroyView()
  }

  private fun onEvent(event: SettingsEvent) {
    when (event) {
      SettingsEvent.DATA_CLEARED -> Unit
      SettingsEvent.DATA_CLEARED_NO_NETWORK -> Unit
      SettingsEvent.PATH_ERROR -> Unit
      SettingsEvent.REQUEST_PATH -> Unit
      SettingsEvent.REQUEST_PERMISSIONS -> Unit
    }
  }


  private fun onNotifyPriceState(notify: Boolean) {
    if (notify) {

    } else {

    }
  }

  private fun onDownloadLce(lceState: LceState) {
    when (lceState) {
      is LceState.Content -> Unit
      is LceState.Loading -> Unit
      is LceState.Error -> Unit
      else -> Unit
    }
  }

  private fun onAuthState(isAuthenticated: Boolean) {
    if (isAuthenticated) {

    } else {

    }
  }

  private fun requestPermissions() {
    requestPermissionsLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
  }

  private fun requestPath() {
    try {
      requestPathLauncher.launch(Uri.EMPTY)
    } catch (exception: ActivityNotFoundException) {
      showTempSnackbar(getString(R.string.errorNoAppToResolve))
    }
  }
}