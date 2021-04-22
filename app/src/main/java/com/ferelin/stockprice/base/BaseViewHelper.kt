package com.ferelin.stockprice.base

import android.content.Context

/*
* Base rules for view helper.
* ViewHelper is a class created for help control view with animations.
* */
abstract class BaseViewHelper {
    abstract fun prepare(context: Context)
    abstract fun invalidate()
}