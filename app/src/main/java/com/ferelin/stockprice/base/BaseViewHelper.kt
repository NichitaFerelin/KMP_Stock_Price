package com.ferelin.stockprice.base

import android.content.Context

abstract class BaseViewHelper {
    abstract fun prepare(context: Context)
    abstract fun invalidate()
}