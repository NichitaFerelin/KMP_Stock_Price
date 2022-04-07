package com.ferelin.stockprice.androidApp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

internal val viewModelWrapperModule = module {
  viewModel { ViewModelWrapper() }
}

internal class ViewModelWrapper : ViewModel(), KoinComponent {
  inline fun <reified T> viewModel(parameter: Any? = null): T {
    val viewModel: T by inject { parametersOf(viewModelScope, parameter) }
    return viewModel
  }
}