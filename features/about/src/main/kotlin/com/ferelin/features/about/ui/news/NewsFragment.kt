package com.ferelin.features.about.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.R
import com.ferelin.core.ui.params.NewsParams
import com.ferelin.core.ui.view.BaseFragment
import com.ferelin.core.ui.view.adapter.ifLinear
import com.ferelin.core.ui.view.adapter.scrollToTopWithCustomAnim
import com.ferelin.core.ui.view.animManager.invalidate
import com.ferelin.core.ui.view.launchAndRepeatWithViewLifecycle
import com.ferelin.core.ui.view.setOnClick
import com.ferelin.features.about.databinding.FragmentNewsBinding
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class NewsFragment(
  params: NewsParams
) : BaseFragment<FragmentNewsBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNewsBinding
    get() = FragmentNewsBinding::inflate

  @Inject
  lateinit var viewModelFactory: NewsViewModelFactory.Factory
  private val viewModel: NewsViewModel by viewModels {
    viewModelFactory.create(params)
  }

  private var fadeOut: Animation? = null
  private var fadeIn: Animation? = null

  override fun initUi() {
    viewBinding.recyclerViewNews.apply {
      adapter = viewModel.newsAdapter
      addItemDecoration(NewsItemDecoration(requireContext()))
    }
    viewBinding.fab.setOnClick(this@NewsFragment::scrollToTop)
  }

  override fun initObservers() {
    with(viewModel) {
      launchAndRepeatWithViewLifecycle {
        news
          .flowOn(Dispatchers.Main)
          .onEach(this@NewsFragment::onNews)
          .launchIn(this)

        newsLce
          .flowOn(Dispatchers.Main)
          .onEach(this@NewsFragment::onNewsLce)
          .launchIn(this)

        networkState
          .flowOn(Dispatchers.Main)
          .onEach(this@NewsFragment::onNetwork)
          .launchIn(this)
      }
    }
  }

  override fun onDestroyView() {
    fadeIn?.invalidate()
    fadeOut?.invalidate()
    viewBinding.recyclerViewNews.adapter = null
    super.onDestroyView()
  }

  private fun onNews(news: List<NewsViewData>) {

  }

  private fun onNewsLce(lceState: LceState) {
    when (lceState) {
      is LceState.Content -> hideProgressBar()
      is LceState.Loading -> showProgressBar()
      is LceState.Error -> {
        hideProgressBar()
        onError()
      }
      else -> Unit
    }
  }

  private fun onNetwork(available: Boolean) {
    if (available) {
      // show snackbar
    } else {
      // show snackbar
    }
  }

  private fun onError() {
    // show error
  }

  private fun showProgressBar() {
    viewBinding.progressBar.isVisible = true
  }

  private fun hideProgressBar() {
    viewBinding.progressBar.isVisible = false
  }

  private fun scrollToTop() {
    viewBinding.recyclerViewNews.layoutManager.ifLinear { layoutManager ->
      if (layoutManager.findFirstVisibleItemPosition() < NEWS_SCROLL_WITH_ANIM_AFTER) {
        viewBinding.recyclerViewNews.smoothScrollToPosition(0)
      } else {
        initFadeAnims()
        viewBinding.recyclerViewNews.scrollToTopWithCustomAnim(
          fadeIn!!,
          fadeOut!!,
          null
        )
      }
    }
  }

  private fun initFadeAnims() {
    if (fadeIn == null) {
      fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
      fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
    }
  }
}

internal const val NEWS_SCROLL_WITH_ANIM_AFTER = 7