package com.ferelin.features.about.news

import com.ferelin.core.domain.entity.CompanyId
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val newsModule = module {
    viewModel { params ->
        val id = get<Int>()
        NewsViewModel(CompanyId(id), get(), get(), get(), get())
    }
}