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

package com.ferelin.feature_loading.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.fragment.app.viewModels
import com.ferelin.core.utils.animManager.MotionManager
import com.ferelin.core.utils.launchAndRepeatWithViewLifecycle
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.feature_loading.R
import com.ferelin.feature_loading.databinding.FragmentLoadingBinding
import com.ferelin.feature_loading.viewModel.LoadingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadingFragment : BaseFragment<FragmentLoadingBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoadingBinding
        get() = FragmentLoadingBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<LoadingViewModel>

    private val viewModel: LoadingViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    override fun initObservers() {
        super.initObservers()
        launchAndRepeatWithViewLifecycle {
            observeFirstTimeLaunch()
        }
    }

    private suspend fun observeFirstTimeLaunch() {
        viewModel.loadPreparedState.collect { loadPrepared ->
            if (loadPrepared) {
                withContext(Dispatchers.Main) {
                    stopAnim()
                }
            }
        }
    }

    private fun stopAnim() {
        viewBinding.root.setTransitionListener(object : MotionManager() {
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                super.onTransitionCompleted(p0, p1)
                removeAutoTransition()
                viewModel.onAnimationsStopped()
            }
        })
    }

    private fun removeAutoTransition() {
        viewBinding.root.getTransition(R.id.transitionMain).autoTransition =
            MotionScene.Transition.AUTO_NONE
        viewBinding.root.getTransition(R.id.transitionJump).autoTransition =
            MotionScene.Transition.AUTO_NONE
    }

    companion object {
        fun newInstance(data: Any?): LoadingFragment {
            return LoadingFragment()
        }
    }
}