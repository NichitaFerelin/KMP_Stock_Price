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

package com.ferelin.core.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.ferelin.shared.DispatchersProvider
import javax.inject.Inject

abstract class BaseFragment<ViewBindingType : ViewBinding> : Fragment() {

    abstract val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBindingType

    private var viewBinding: ViewBindingType? = null
    protected val mViewBinding: ViewBindingType
        get() = checkNotNull(viewBinding)

    @Inject
    lateinit var mDispatchersProvider: DispatchersProvider

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // TODO inject deps
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = mBindingInflater.invoke(inflater, container, false)
        return mViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initUx()
        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    open fun initUi() {
        // Override in subclasses
    }

    open fun initUx() {
        // Override in subclasses
    }

    open fun initObservers() {
        // Override in subclasses
    }

    fun postponeTransitions(fragment: Fragment) {
        fragment.postponeEnterTransition()
        fragment
            .requireView()
            .doOnPreDraw { fragment.startPostponedEnterTransition() }
    }
}