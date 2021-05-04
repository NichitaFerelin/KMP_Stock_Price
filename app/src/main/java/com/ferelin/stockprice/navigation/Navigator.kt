package com.ferelin.stockprice.navigation

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
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }
}