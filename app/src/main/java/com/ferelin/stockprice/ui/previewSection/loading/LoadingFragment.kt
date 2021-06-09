package com.ferelin.stockprice.ui.previewSection.loading

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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentLoadingBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoadingFragment :
    BaseFragment<FragmentLoadingBinding, LoadingViewModel, LoadingViewController>() {

    override val mViewController = LoadingViewController()
    override val mViewModel: LoadingViewModel by viewModels()

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoadingBinding
        get() = FragmentLoadingBinding::inflate

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            collectStateFirstTimeLaunch()
        }
    }

    private suspend fun collectStateFirstTimeLaunch() {
        mViewModel.isFirstTimeLaunchState.collect { isFirstTimeLaunch ->
            mViewController.onFirstTimeStateChanged(this, isFirstTimeLaunch)
        }
    }
}