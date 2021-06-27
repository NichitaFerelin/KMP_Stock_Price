package com.ferelin.stockprice.ui.previewSection.welcome

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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.App
import com.ferelin.stockprice.ui.MainActivity
import com.ferelin.stockprice.utils.compose.StockPriceTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WelcomeFragment : Fragment() {

    private val mViewModel: WelcomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                StockPriceTheme {
                    Welcome(mViewModel)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            mViewModel.actionMoveToNextScreen.collect { move ->
                if (move) {
                    val hostActivity = requireActivity()
                    if (hostActivity is MainActivity) {
                        hostActivity.navigator.navigateToDrawerHostFragment()
                    }
                }
            }
        }
    }

    private fun injectDependencies() {
        val hostApplication = requireActivity().application
        if (hostApplication is App) {
            hostApplication.appComponent.inject(mViewModel)
        } else throw IllegalStateException(
            "WelcomeFragment cannot inject dependencies. Host application is not '.App' class"
        )
    }
}
