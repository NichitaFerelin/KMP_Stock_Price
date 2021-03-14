package com.ferelin.stockprice.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.MainActivity

abstract class BaseFragment<out T : BaseViewModel>(
    protected val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : Fragment() {

    protected abstract val mViewModel: T
    protected lateinit var mDataInteractor: DataInteractor

    protected abstract fun setUpViewComponents()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mDataInteractor = (activity as MainActivity).dataInteractor
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewComponents()
        initObservers()
    }

    protected open fun initObservers() {
        mViewModel.initObservers()
    }
}