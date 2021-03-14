package com.ferelin.stockprice.viewModelFactories

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.aboutSection.aboutPager.AboutPagerViewModel
import com.ferelin.stockprice.ui.aboutSection.chart.ChartViewModel
import com.ferelin.stockprice.ui.aboutSection.news.NewsViewModel
import kotlinx.coroutines.FlowPreview

@Suppress("UNCHECKED_CAST")
class ArgsViewModelFactory(
    private val mCoroutineContext: CoroutineContextProvider,
    private val mDataInteractor: DataInteractor,
    private val mArguments: Bundle?
) : DataViewModelFactory(mCoroutineContext, mDataInteractor) {

    @FlowPreview
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AboutPagerViewModel::class.java) -> {
                AboutPagerViewModel(mCoroutineContext, mDataInteractor, mArguments) as T
            }
            modelClass.isAssignableFrom(NewsViewModel::class.java) -> {
                NewsViewModel(mCoroutineContext, mDataInteractor, mArguments) as T
            }
            modelClass.isAssignableFrom(ChartViewModel::class.java) -> {
                ChartViewModel(mCoroutineContext, mDataInteractor, mArguments) as T
            }

            else -> throw IllegalStateException("Unknown view model class: $modelClass")
        }
    }
}