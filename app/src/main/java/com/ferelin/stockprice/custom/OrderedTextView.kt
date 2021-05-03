package com.ferelin.stockprice.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.ferelin.stockprice.R

/*
* TextView with "orderNumber" attribute
* */
class OrderedTextView(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var mOrderNumber: Int
    val orderNumber: Int
        get() = mOrderNumber

    init {
        val typedArr = context.obtainStyledAttributes(attrs, R.styleable.OrderedTextView)
        mOrderNumber = typedArr.getInt(R.styleable.OrderedTextView_orderNumber, 0)
        typedArr.recycle()
    }
}