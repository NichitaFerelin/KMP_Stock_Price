package com.ferelin.features.about.about

import com.ferelin.core.domain.entity.CompanyId
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val aboutModule = module {
    viewModel { params ->
        val id = params.get<Int>()
        AboutViewModel(CompanyId(id), get(), get(), get(), get())
    }
}