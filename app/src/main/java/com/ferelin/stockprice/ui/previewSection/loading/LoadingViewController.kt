package com.ferelin.stockprice.ui.previewSection.loading

import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.databinding.FragmentLoadingBinding
import com.ferelin.stockprice.ui.previewSection.welcome.WelcomeFragment
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment
import com.ferelin.stockprice.utils.anim.MotionManager
import kotlinx.coroutines.launch

class LoadingViewController : BaseViewController<LoadingViewAnimator, FragmentLoadingBinding>() {

    override val mViewAnimator: LoadingViewAnimator = LoadingViewAnimator()

    fun onFirstTimeStateChanged(fragmentManager: FragmentManager, isFirstTimeLaunch: Boolean?) {
        isFirstTimeLaunch?.let { setUpTransitionListener(fragmentManager, it) }
    }

    private fun setUpTransitionListener(
        fragmentManager: FragmentManager,
        isFirstTimeLaunch: Boolean
    ) {
        viewBinding!!.root.setTransitionListener(object : MotionManager() {
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                super.onTransitionCompleted(p0, p1)
                removeAutoTransition()
                replaceFragment(fragmentManager, isFirstTimeLaunch)
            }
        })
    }

    // Stop transition cycle
    private fun removeAutoTransition() {
        viewBinding!!.root.getTransition(R.id.transitionMain).autoTransition =
            MotionScene.Transition.AUTO_NONE
        viewBinding!!.root.getTransition(R.id.transitionJump).autoTransition =
            MotionScene.Transition.AUTO_NONE
    }

    private fun replaceFragment(fragmentManager: FragmentManager, isFirstTimeLaunch: Boolean) {
        mViewLifecycleScope!!.launch(mCoroutineContext.IO) {
            val fragment = when {
                isFirstTimeLaunch -> WelcomeFragment()
                else -> StocksPagerFragment()
            }
            fragmentManager.commit {
                replace(R.id.fragmentContainer, fragment)
            }
        }
    }
}