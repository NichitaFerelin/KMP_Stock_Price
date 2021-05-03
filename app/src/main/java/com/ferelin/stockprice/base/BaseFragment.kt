package com.ferelin.stockprice.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.MainActivity

/**
 * [BaseFragment] is the fundament for fragments.
 *
 *  In the architecture of this application, the inheritors of this
 *  class have very simple logic, which consists of setting listeners
 *  on the view and notifying about clicking in other components
 *  such a [BaseViewModel] or [BaseViewController].
 *
 *
 *  All view UI logic is delegated to [BaseViewController].
 *
 *  All logic about getting data to display is delegated to [BaseViewModel].
 *
 *
 *  Example:
 *   1. Fragment begins observing ViewModel data-event state.
 *   2. ViewModel sends a request to the network for data.
 *   3. ViewModel updates the state of data.
 *   4. Fragment receives data and sends it to ViewController.
 *   5. ViewController display data with animations or whatever.
 *
 */
abstract class BaseFragment<
        ViewBindingType,
        out ViewModelType : BaseViewModel,
        out ViewControllerType : BaseViewController<BaseViewAnimator, ViewBindingType>>(
    protected val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : Fragment() {

    protected abstract val mViewModel: ViewModelType
    protected abstract val mViewController: ViewControllerType
    protected lateinit var mDataInteractor: DataInteractor

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mDataInteractor = (activity as MainActivity).dataInteractor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewController.onCreateFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        * Is important to call it before collecting any ViewModel states to avoid threads conflicts.
        * */
        mViewModel.triggerCreate()

        setUpViewComponents(savedInstanceState)
        initObservers()
    }

    protected open fun setUpViewComponents(savedInstanceState: Bundle?) {
        mViewController.onViewCreated(
            savedInstanceState,
            this@BaseFragment,
            viewLifecycleOwner.lifecycleScope
        )
    }

    protected open fun initObservers() {
        mViewModel.initObservers()
    }

    override fun onDestroyView() {
        mViewController.onDestroyView()
        super.onDestroyView()
    }
}