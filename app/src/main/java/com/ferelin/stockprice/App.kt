package com.ferelin.stockprice

import android.app.Application
import com.ferelin.stockprice.dataInteractor.DataInteractor

class App : Application() {

    val dataInteractor: DataInteractor by lazy {
        DataInteractor.getInstance(this)
    }
}