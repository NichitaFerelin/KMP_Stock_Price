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

package com.ferelin.feature_loading

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.core.base.BaseFragment
import com.ferelin.core.base.BaseViewModelFactory
import com.ferelin.feature_loading.databinding.FragmentLoadingBinding
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class LoadingFragment : BaseFragment<FragmentLoadingBinding>() {

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoadingBinding
        get() = FragmentLoadingBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<LoadingViewModel>

    private val mViewModel: LoadingViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            observeFirstTimeLaunch()
        }
    }

    private suspend fun observeFirstTimeLaunch() {
        mViewModel.firstTimeLaunch.collect { loadState ->
            when(loadState) {
                is FirstTimeLoadState.Loaded -> {
                    // update ui
                }
                is FirstTimeLoadState.Loading -> {
                    // update ui
                }
                is FirstTimeLoadState.None -> {
                    mViewModel.loadFirstTimeLaunch()
                }
            }
        }
    }

    /*
    * private fun initFragmentReplace(isFirstTimeLaunch: Boolean) {
        viewBinding.root.setTransitionListener(object : MotionManager() {
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                super.onTransitionCompleted(p0, p1)
                removeAutoTransition()
                replaceFragment(isFirstTimeLaunch)
            }
        })
    }

    // Stops transition cycle
    private fun removeAutoTransition() {
        viewBinding.root.getTransition(R.id.transitionMain).autoTransition =
            MotionScene.Transition.AUTO_NONE
        viewBinding.root.getTransition(R.id.transitionJump).autoTransition =
            MotionScene.Transition.AUTO_NONE
    }

    private fun replaceFragment(isFirstTimeLaunch: Boolean) {
        if (isFirstTimeLaunch) {
            mNavigator?.navigateToWelcomeFragment()
        } else mNavigator?.navigateToDrawerHostFragment()
    }
    * */
}