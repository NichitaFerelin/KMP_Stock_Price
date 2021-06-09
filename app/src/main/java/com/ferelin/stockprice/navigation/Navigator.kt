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
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.ui.MainActivity
import com.ferelin.stockprice.ui.aboutSection.aboutSection.AboutPagerFragment
import com.ferelin.stockprice.ui.bottomDrawerSection.login.LoginFragment
import com.ferelin.stockprice.ui.bottomDrawerSection.menu.MenuFragment
import com.ferelin.stockprice.ui.previewSection.loading.LoadingFragment
import com.ferelin.stockprice.ui.previewSection.welcome.WelcomeFragment
import com.ferelin.stockprice.ui.stocksSection.search.SearchFragment
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment

/**
 * [Navigator] represents a class that provides ability to navigate between fragments.
 */
object Navigator {

    private const val sStackNameMain = "main-stack"
    private const val sStackNameBottomDrawer = "bottom-stack"

    fun navigateToLoadingFragment(currentActivity: MainActivity) {
        currentActivity.run {
            if (currentActivity.supportFragmentManager.fragments.isEmpty()) {
                supportFragmentManager.commit {
                    add(R.id.fragmentContainer, LoadingFragment())
                }
            }
        }
    }

    fun navigateToWelcomeFragment(currentFragment: Fragment) {
        currentFragment.parentFragmentManager.commit {
            replace(R.id.fragmentContainer, WelcomeFragment())
        }
    }

    fun navigateToStocksPagerFragment(currentFragment: Fragment) {
        currentFragment.parentFragmentManager.commit {
            replace(R.id.fragmentContainer, StocksPagerFragment())
        }
    }

    fun navigateToSearchFragment(
        currentFragment: Fragment,
        onCommit: ((FragmentTransaction) -> Unit)? = null
    ) {
        currentFragment.parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragmentContainer, SearchFragment())
            addToBackStack(sStackNameMain)
            onCommit?.invoke(this)
        }
    }

    fun navigateToAboutPagerFragment(
        selectedCompany: AdaptiveCompany,
        fragmentManager: FragmentManager,
        onCommit: ((FragmentTransaction) -> Unit)? = null
    ) {
        fragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragmentContainer, AboutPagerFragment(selectedCompany))
            addToBackStack(sStackNameMain)
            onCommit?.invoke(this)
        }
    }

    fun navigateToMenuFragment(currentFragment: Fragment) {
        currentFragment.childFragmentManager.commit {
            replace(R.id.containerBottom, MenuFragment())
            addToBackStack(sStackNameBottomDrawer)
        }
    }

    fun navigateToLoginFragment(currentFragment: Fragment) {
        currentFragment.parentFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_right,
                R.anim.slide_left,
                R.anim.slide_right,
                R.anim.slide_left
            )
            replace(R.id.containerBottom, LoginFragment())
            addToBackStack(sStackNameBottomDrawer)
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