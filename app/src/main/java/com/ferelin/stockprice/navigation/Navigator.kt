package com.ferelin.stockprice.navigation

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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.R
import com.ferelin.stockprice.ui.MainActivity
import com.ferelin.stockprice.ui.aboutSection.aboutSection.AboutPagerFragment
import com.ferelin.stockprice.ui.previewSection.loading.LoadingFragment
import com.ferelin.stockprice.ui.previewSection.welcome.WelcomeFragment
import com.ferelin.stockprice.ui.stocksSection.search.SearchFragment
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment
import kotlinx.coroutines.launch

/*
* App navigator
* */
object Navigator {

    var coroutineContextProvider: CoroutineContextProvider = CoroutineContextProvider()

    fun navigateToLoadingFragment(activity: MainActivity) {
        activity.lifecycleScope.launch(coroutineContextProvider.IO) {
            if (activity.supportFragmentManager.fragments.isEmpty()) {
                activity.supportFragmentManager.commit {
                    add(R.id.fragmentContainer, LoadingFragment())
                }
            }
        }
    }

    fun navigateToWelcomeFragment(fragment: Fragment) {
        fragment.viewLifecycleOwner.lifecycleScope.launch(coroutineContextProvider.IO) {
            fragment.parentFragmentManager.commit {
                replace(R.id.fragmentContainer, WelcomeFragment())
            }
        }
    }

    fun navigateToStocksPagerFragment(fragment: Fragment) {
        fragment.viewLifecycleOwner.lifecycleScope.launch(coroutineContextProvider.IO) {
            fragment.parentFragmentManager.commit {
                replace(R.id.fragmentContainer, StocksPagerFragment())
            }
        }
    }

    fun navigateToSearchFragment(
        fragment: Fragment,
        onCommit: ((FragmentTransaction) -> Unit)? = null
    ) {
        fragment.viewLifecycleOwner.lifecycleScope.launch(coroutineContextProvider.IO) {
            fragment.parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainer, SearchFragment())
                addToBackStack(null)
                onCommit?.invoke(this)
            }
        }
    }

    fun navigateToAboutPagerFragment(
        fragment: Fragment,
        company: AdaptiveCompany,
        fragmentManager: FragmentManager,
        onCommit: ((FragmentTransaction) -> Unit)? = null
    ) {
        fragment.viewLifecycleOwner.lifecycleScope.launch(coroutineContextProvider.IO) {
            fragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainer, AboutPagerFragment(company))
                addToBackStack(null)
                onCommit?.invoke(this)
            }
        }
    }

    fun navigateToUrl(context: Context, url: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        return launchIntent(intent, context)
    }

    fun navigateToContacts(context: Context, phone: String): Boolean {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
        return launchIntent(intent, context)
    }

    private fun launchIntent(intent: Intent, context: Context): Boolean {
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }
}