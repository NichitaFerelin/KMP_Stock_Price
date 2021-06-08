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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
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
        ViewBindingType : ViewBinding,
        out ViewModelType : BaseViewModel,
        out ViewControllerType : BaseViewController<BaseViewAnimator, ViewBindingType>>(
    protected val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : Fragment() {

    protected abstract val mViewModel: ViewModelType
    protected abstract val mViewController: ViewControllerType
    protected lateinit var mDataInteractor: DataInteractor

    abstract val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBindingType

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mDataInteractor = (activity as MainActivity).dataInteractor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewController.onCreateFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = mBindingInflater.invoke(inflater, container, false)
        mViewController.viewBinding = viewBinding
        return viewBinding.root
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mViewController.onSaveInstanceState(outState)
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