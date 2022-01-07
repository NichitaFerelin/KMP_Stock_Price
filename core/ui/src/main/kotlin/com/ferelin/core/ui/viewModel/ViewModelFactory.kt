package com.ferelin.core.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class BaseViewModelFactory<VM : ViewModel> @Inject constructor(
  private val viewModel: VM
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return viewModel as T
  }
}