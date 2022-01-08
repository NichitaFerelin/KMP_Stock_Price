package com.ferelin.features.about.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.params.NewsParams
import com.ferelin.core.ui.view.BaseFragment
import com.ferelin.core.ui.view.launchAndRepeatWithViewLifecycle
import com.ferelin.features.about.databinding.FragmentNewsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class NewsFragment : BaseFragment<FragmentNewsBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNewsBinding
    get() = FragmentNewsBinding::inflate

  @Inject
  lateinit var viewModelFactory: NewsViewModelFactory.Factory
  private val viewModel: NewsViewModel by viewModels {
    val params = requireArguments()[NEWS_SCREEN_KEY] as NewsParams
    viewModelFactory.create(params)
  }

  override fun initUi() {
    viewBinding.recyclerViewNews.apply {
      adapter = viewModel.newsAdapter
      addItemDecoration(NewsItemDecoration(requireContext()))
    }
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
          .onEach { /*TODO*/ }
          .launchIn(this)
      }
    }
  }

  override fun onDestroyView() {
    viewBinding.recyclerViewNews.adapter = null
    super.onDestroyView()
  }

  private fun onNews(news: List<NewsViewData>) {
    viewModel.newsAdapter.setData(news)
  }

  private fun onNewsLce(lceState: LceState) {
    when (lceState) {
      is LceState.Content -> Unit
      is LceState.Loading -> Unit
      is LceState.Error -> Unit
      else -> Unit
    }
  }
}