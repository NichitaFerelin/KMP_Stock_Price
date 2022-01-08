package com.ferelin.features.splash.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.fragment.app.viewModels
import com.ferelin.core.ui.view.BaseFragment
import com.ferelin.core.ui.view.animManager.MotionManager
import com.ferelin.core.ui.view.launchAndRepeatWithViewLifecycle
import com.ferelin.core.ui.viewModel.BaseViewModelFactory
import com.ferelin.features.splash.R
import com.ferelin.features.splash.databinding.FragmentLoadingBinding
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class LoadingFragment : BaseFragment<FragmentLoadingBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoadingBinding
    get() = FragmentLoadingBinding::inflate

  @Inject
  lateinit var viewModelFactory: Lazy<BaseViewModelFactory<LoadingViewModel>>
  private val viewModel: LoadingViewModel by viewModels(
    factoryProducer = { viewModelFactory.get() }
  )

  override fun initObservers() {
    launchAndRepeatWithViewLifecycle {
      viewModel.launchTrigger
        .filter { it }
        .map { }
        .flowOn(Dispatchers.Main)
        .onEach(this@LoadingFragment::stopAnimAndLaunch)
        .launchIn(this)
    }
  }

  private fun stopAnimAndLaunch(unit: Unit) {
    viewBinding.root.setTransitionListener(object : MotionManager() {
      override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
        super.onTransitionCompleted(p0, p1)
        viewBinding.root.apply {
          getTransition(R.id.transitionMain).autoTransition = MotionScene.Transition.AUTO_NONE
          getTransition(R.id.transitionJump).autoTransition = MotionScene.Transition.AUTO_NONE
        }
        viewModel.onPrepared()
      }
    })
  }
}

internal const val SPLASH_SCREEN_LIFE_TIME = 2000L