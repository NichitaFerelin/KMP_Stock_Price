package com.ferelin.stockprice

import android.app.Application
import com.ferelin.stockprice.dataInteractor.DataInteractor

class App : Application() {

    private lateinit var mDataInteractor: DataInteractor
    val dataInteractor: DataInteractor
        get() = mDataInteractor

    override fun onCreate() {
        super.onCreate()
        mDataInteractor = DataInteractor.getInstance(this)
    }
}