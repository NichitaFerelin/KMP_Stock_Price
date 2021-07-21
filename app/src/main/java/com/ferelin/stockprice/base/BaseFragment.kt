package com.ferelin.stockprice.base

/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.App
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.MainActivity
import com.ferelin.stockprice.utils.TimerTasks

/**
 * [BaseFragment] is the fundament for fragments.
 *
 * [BaseFragment] represent a Fragment with minimal logic(usually it is
 * only 'setOnClickListeners' or collecting view model states logic.
 * Listeners notifies about click to [BaseViewController] or [BaseViewModel])
 *
 *
 *  All UI logic is delegated to [BaseViewController].
 *
 *  All logic about getting data to display is delegated to [BaseViewModel].
 *
 *
 *  Example:
 *   1. Fragment begins observing ViewModel data-event state.
 *   2. ViewModel sends a request to the [DataInteractor] for data.
 *   3. ViewModel updates the state of data.
 *   4. Fragment receives data and sends it to ViewController.
 *   5. ViewController displays data with animations or whatever.
 */
abstract class BaseFragment<
        ViewBindingType : ViewBinding,
        ViewModelType : BaseViewModel,
        ViewControllerType : BaseViewController<BaseViewAnimator, ViewBindingType>> :
    Fragment() {

    protected abstract val mViewController: ViewControllerType
    protected abstract val mViewModel: ViewModelType

    protected val mCoroutineContext = CoroutineContextProvider()

    private val mHostActivity: MainActivity?
        get() = if (requireActivity() is MainActivity) {
            requireActivity() as MainActivity
        } else null

    // Hides bottom bar on scroll
    protected val mOnScrollListener: RecyclerView.OnScrollListener by lazy(LazyThreadSafetyMode.NONE) {
        object : RecyclerView.OnScrollListener() {

            private var mIsHidden = false

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && !mIsHidden) {
                    mIsHidden = true
                    hideBottomBar()
                } else if (dy < 0 && mIsHidden) {
                    mIsHidden = false
                    showBottomBar()
                }
            }
        }
    }

    /**
     * The application uses view binding.
     * To avoid a lot of repetitive code, view inflates in the base class. For such an
     * implementation, need to override this variable in the subclass.
     * */
    abstract val mBindingInflater: ((LayoutInflater, ViewGroup?, Boolean) -> ViewBindingType)?

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injectDependencies()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNavigator()
        mViewController.onCreateFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBindingInflater?.let { bindingInflater ->
            val viewBinding = bindingInflater.invoke(inflater, container, false)
            mViewController.attachViewBinding(viewBinding)
            return@let viewBinding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.triggerCreate()

        setUpBackPressedCallback()
        setUpViewComponents(savedInstanceState)
        initObservers()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mViewController.onSaveInstanceState(outState)
    }

    protected open fun setUpViewComponents(savedInstanceState: Bundle?) {
        mViewController.onViewCreated(savedInstanceState, this@BaseFragment)
    }

    protected open fun initObservers() {
        mViewModel.initObservers()
    }

    /**
     * Fragments with custom 'BackPressed' logic or bottom app-bar can override this method to avoid
     * bottom app bar state inconsistency.
     * */
    protected open fun onBackPressedHandle(): Boolean {
        return false
    }

    override fun onDestroyView() {
        mViewController.onDestroyView()
        super.onDestroyView()
    }

    private fun setUpBackPressedCallback() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    /**
                     * First need to check that onBack was not pressed to close the bottom
                     * drawer fragment.
                     * */
                    if (handleBottomDrawerOnBack()) {
                        return
                    }

                    /**
                     * Then the fragment gets ability to handle onBack press
                     * */
                    if (onBackPressedHandle()) {
                        return
                    }

                    this.remove()
                    activity?.onBackPressed()
                }
            })
    }

    private fun handleBottomDrawerOnBack(): Boolean {
        return mHostActivity?.handleOnBackPressed() ?: false
    }

    private fun injectDependencies() {
        val application = requireActivity().application
        if (application is App) {
            application.appComponent.inject(mViewModel)
        }
    }

    private fun initNavigator() {
        mHostActivity?.let { mViewController.setNavigator(it.navigator) }
    }

    private fun hideBottomBar() {
        TimerTasks.withTimerOnUi(200) {
            mHostActivity?.hideBottomBar()
        }
    }

    private fun showBottomBar() {
        TimerTasks.withTimerOnUi(200) {
            mHostActivity?.showBottomBar()
        }
    }
}