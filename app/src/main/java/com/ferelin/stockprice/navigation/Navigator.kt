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
import com.ferelin.repository.adaptiveModels.AdaptiveChat
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.ui.MainActivity
import com.ferelin.stockprice.ui.aboutSection.aboutPager.AboutPagerFragment
import com.ferelin.stockprice.ui.login.LoginFragment
import com.ferelin.stockprice.ui.messagesSection.chat.ChatFragment
import com.ferelin.stockprice.ui.messagesSection.chats.ChatsFragment
import com.ferelin.stockprice.ui.previewSection.loading.LoadingFragment
import com.ferelin.stockprice.ui.previewSection.welcome.WelcomeFragment
import com.ferelin.stockprice.ui.stocksSection.search.SearchFragment
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment
import com.ferelin.stockprice.utils.TimerTasks
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [Navigator] represents a class that provides ability to navigate between fragments.
 *  Controls bottom app bar visibility state.
 */
@Singleton
class Navigator @Inject constructor() {

    /**
     * To resolve fragment replace
     * */
    private var mHostActivity: MainActivity? = null

    /**
     * Fragment tags
     * */
    private val mChatsTag = "chats"
    private val mStocksPagerTag = "stocksPager"

    private var mIsOnScreenWithMenu = false
    val isOnScreenWithMenu: Boolean
        get() = mIsOnScreenWithMenu

    fun attachHostActivity(activity: MainActivity) {
        mHostActivity = activity
    }

    fun detachHostActivity() {
        mHostActivity = null
    }

    fun navigateToLoadingFragment() {
        replaceMainContainerBy(LoadingFragment())
    }

    fun navigateToLoginFragment(isReplacedFromMenu: Boolean) {
        mIsOnScreenWithMenu = false
        hideBottomBar()
        replaceMainContainerBy(LoginFragment(isReplacedFromMenu), addToBackStack = true)
    }

    fun navigateToChatFragment(chat: AdaptiveChat) {
        mIsOnScreenWithMenu = false
        hideBottomBar()
        replaceMainContainerBy(ChatFragment(chat), addToBackStack = true)
    }

    fun navigateToChatsFragment() {
        if (!contains(mChatsTag)) {
            mIsOnScreenWithMenu = true
            replaceMainContainerBy(ChatsFragment(), mChatsTag)
        }
    }

    fun navigateToWelcomeFragment() {
        replaceMainContainerBy(WelcomeFragment())
    }

    fun navigateToStocksPagerFragment() {
        if (!contains(mStocksPagerTag)) {
            mIsOnScreenWithMenu = true
            replaceMainContainerBy(StocksPagerFragment(), mStocksPagerTag)
        }
    }

    fun navigateToDrawerHostFragment() {
        mIsOnScreenWithMenu = true
        TimerTasks.withTimerOnUi(500) { mHostActivity?.showBottomBar() }
        replaceMainContainerBy(StocksPagerFragment(), mStocksPagerTag)
    }

    fun navigateToSearchFragment(onCommit: ((FragmentTransaction) -> Unit)? = null) {
        mIsOnScreenWithMenu = false
        hideBottomBar()
        replaceMainContainerBy(SearchFragment(), addToBackStack = true, onCommit = onCommit)
    }

    fun navigateToAboutPagerFragment(
        selectedCompany: AdaptiveCompany,
        fragmentManager: FragmentManager
    ) {
        hideBottomBar()
        fragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.fragmentContainer,
                AboutPagerFragment(selectedCompany, mIsOnScreenWithMenu)
            )
            addToBackStack(null)
        }
        mIsOnScreenWithMenu = false
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

    fun navigateFromAboutPagerToSearch() {
        mHostActivity?.supportFragmentManager?.popBackStack()
    }

    fun navigateBackToHostFragment() {
        mIsOnScreenWithMenu = true
        mHostActivity?.supportFragmentManager?.popBackStack()
        TimerTasks.withTimerOnUi(250) { mHostActivity?.showBottomBar() }
    }

    private fun contains(tag: String): Boolean {
        return mHostActivity
            ?.supportFragmentManager
            ?.fragments
            ?.find { it.tag == tag } != null
    }

    private fun replaceMainContainerBy(
        fragment: Fragment,
        tag: String? = null,
        addToBackStack: Boolean = false,
        onCommit: ((FragmentTransaction) -> Unit)? = null
    ) {
        mHostActivity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            replace(R.id.fragmentContainer, fragment, tag)
            if (addToBackStack) addToBackStack(null)
            onCommit?.invoke(this)
        }
    }

    private fun launchIntent(intent: Intent, context: Context): Boolean {
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }

    private fun hideBottomBar() {
        TimerTasks.withTimerOnUi(200) {
            mHostActivity?.hideBottomBar()
        }
    }
}