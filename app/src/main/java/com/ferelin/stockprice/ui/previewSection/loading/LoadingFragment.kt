package com.ferelin.stockprice.ui.previewSection.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentLoadingBinding
import com.ferelin.stockprice.ui.previewSection.welcome.WelcomeFragment
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment
import com.ferelin.stockprice.utils.anim.MotionManager
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoadingFragment : BaseFragment<LoadingViewModel, LoadingViewHelper>() {

    override val mViewHelper: LoadingViewHelper = LoadingViewHelper()
    override val mViewModel: LoadingViewModel by viewModels {
        DataViewModelFactory(mCoroutineContext, mDataInteractor)
    }

    private var mBinding: FragmentLoadingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentLoadingBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mViewModel.moveToNextScreen.collect { isFirstTimeLaunch ->
                isFirstTimeLaunch?.let { setUpTransitionListener(it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun setUpTransitionListener(isFirstTimeLaunch: Boolean) {
        mBinding!!.root.setTransitionListener(object : MotionManager() {
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                super.onTransitionCompleted(p0, p1)
                removeAutoTransition()
                replaceFragment(isFirstTimeLaunch)
            }
        })
    }

    private fun replaceFragment(isFirstTimeLaunch: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            val fragment = when {
                isFirstTimeLaunch -> WelcomeFragment()
                else -> StocksPagerFragment()
            }
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, fragment)
            }
        }
    }

    private fun removeAutoTransition() {
        mBinding!!.root.apply {
            getTransition(R.id.transitionMain).autoTransition = MotionScene.Transition.AUTO_NONE
            getTransition(R.id.transitionJump).autoTransition = MotionScene.Transition.AUTO_NONE
        }
    }
}