package com.ferelin.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class ViewModelWrapper : ViewModel(), KoinComponent {
  inline fun <reified T> viewModel(): T {
    val viewModel: T by inject { parametersOf(viewModelScope) }
    return viewModel
  }
}