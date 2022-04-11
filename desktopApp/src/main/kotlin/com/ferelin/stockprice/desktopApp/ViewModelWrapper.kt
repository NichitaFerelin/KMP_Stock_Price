package com.ferelin.stockprice.desktopApp

import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

internal class ViewModelWrapper : KoinComponent {
    inline fun <reified T> viewModel(
        viewModelScope: CoroutineScope,
        parameter: Any? = null
    ): T {
        val viewModel: T by inject { parametersOf(viewModelScope, parameter) }
        return viewModel
    }
}