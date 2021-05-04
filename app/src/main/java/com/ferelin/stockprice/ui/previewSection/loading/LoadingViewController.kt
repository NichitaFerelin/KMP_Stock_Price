package com.ferelin.stockprice.ui.previewSection.loading

import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionScene
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.databinding.FragmentLoadingBinding
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.utils.anim.MotionManager

class LoadingViewController : BaseViewController<LoadingViewAnimator, FragmentLoadingBinding>() {

    override val mViewAnimator: LoadingViewAnimator = LoadingViewAnimator()

    fun onFirstTimeStateChanged(fragment: LoadingFragment, isFirstTimeLaunch: Boolean?) {
        isFirstTimeLaunch?.let { setUpTransitionListener(fragment, it) }
    }

    private fun setUpTransitionListener(
        fragment: LoadingFragment,
        isFirstTimeLaunch: Boolean
    ) {
        viewBinding!!.root.setTransitionListener(object : MotionManager() {
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                super.onTransitionCompleted(p0, p1)
                removeAutoTransition()
                replaceFragment(fragment, isFirstTimeLaunch)
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

    private fun replaceFragment(fragment: LoadingFragment, isFirstTimeLaunch: Boolean) {
        if (isFirstTimeLaunch) {
            Navigator.navigateToWelcomeFragment(fragment)
        } else Navigator.navigateToStocksPagerFragment(fragment)
    }
}