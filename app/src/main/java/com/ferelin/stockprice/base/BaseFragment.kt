package com.ferelin.stockprice.base

import android.content.Context
import androidx.fragment.app.Fragment
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.MainActivity

abstract class BaseFragment : Fragment() {

    protected lateinit var mDataInteractor: DataInteractor

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mDataInteractor = (activity as MainActivity).dataInteractor
    }
}