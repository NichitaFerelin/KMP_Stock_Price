package com.ferelin.features.stocks.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.view.BaseFragment
import com.ferelin.core.ui.view.launchAndRepeatWithViewLifecycle
import com.ferelin.core.ui.view.setOnClick
import com.ferelin.core.ui.viewModel.BaseViewModelFactory
import com.ferelin.features.stocks.R
import com.ferelin.features.stocks.databinding.FragmentStocksPagerBinding
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

internal class CommonFragment : BaseFragment<FragmentStocksPagerBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStocksPagerBinding
    get() = FragmentStocksPagerBinding::inflate

  @Inject
  lateinit var viewModelFactory: Lazy<BaseViewModelFactory<CommonViewModel>>
  private val viewModel: CommonViewModel by viewModels(
    factoryProducer = { viewModelFactory.get() }
  )

  private val backPressedCallback by lazy(NONE) {
    object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        if (viewBinding.viewPager.currentItem != 0) {
          viewBinding.viewPager.setCurrentItem(0, true)
        } else {
          this.remove()
          requireActivity().onBackPressed()
        }
      }
    }
  }

  private val viewPagerChangeCallback by lazy(NONE) {
    object : ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        switchTextStyles(position)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enterTransition = MaterialFadeThrough()
      .apply { duration = 300L }
    exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
      .apply { duration = 200L }
    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
      .apply { duration = 200L }
  }

  override fun initUi() {
    with(viewBinding) {
      viewPager.adapter = StocksPagerAdapter(
        childFragmentManager,
        viewLifecycleOwner.lifecycle
      )
      viewPager.registerOnPageChangeCallback(viewPagerChangeCallback)
      recyclerViewCrypto.apply {
        adapter = viewModel.cryptoAdapter
        addItemDecoration(CryptoItemDecoration(requireContext()))
        setHasFixedSize(true)
      }
    }
  }

  override fun initUx() {
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      backPressedCallback
    )

    with(viewBinding) {
      textViewHintStocks.setOnClick(this@CommonFragment::onHintStocksClick)
      textViewHintFavourite.setOnClick(this@CommonFragment::onHistFavouritesClick)
      fab.setOnClick(this@CommonFragment::onFabClick)
      imageSettings.setOnClick(viewModel::onSettingsClick)

      cardViewSearch.setOnClick {
        viewModel.onSearchCardClick(
          sharedElement = viewBinding.toolbar,
          name = requireContext().resources.getString(R.string.transitionSearchFragment)
        )
      }
    }
  }

  override fun initObservers() {
    with(viewModel) {
      launchAndRepeatWithViewLifecycle {
        cryptos
          .flowOn(Dispatchers.Main)
          .onEach(this@CommonFragment::onCryptos)
          .launchIn(this)

        cryptosLce
          .flowOn(Dispatchers.Main)
          .onEach(this@CommonFragment::onCryptosLce)
          .launchIn(this)

        networkState
          .flowOn(Dispatchers.Main)
          .onEach(this@CommonFragment::onNetwork)
          .launchIn(this)
      }
    }
  }

  override fun onDestroyView() {
    viewBinding.viewPager.unregisterOnPageChangeCallback(viewPagerChangeCallback)
    viewBinding.recyclerViewCrypto.adapter = null
    super.onDestroyView()
  }

  private fun onCryptos(cryptos: List<CryptoViewData>) {
    viewModel.cryptoAdapter.setData(cryptos)
  }

  private fun onCryptosLce(lceState: LceState) {
    when (lceState) {
      is LceState.Content -> viewBinding.progressBarCrypto.isVisible = false
      is LceState.Loading -> viewBinding.progressBarCrypto.isVisible = true
      is LceState.Error -> {}
      else -> Unit
    }
  }

  private fun onNetwork(isAvailable: Boolean) {
    // notify
  }

  private fun onHintStocksClick() {
    if (viewBinding.viewPager.currentItem != STOCK_SCREEN_POSITION) {
      viewBinding.viewPager.setCurrentItem(STOCK_SCREEN_POSITION, true)
    }
  }

  private fun onHistFavouritesClick() {
    if (viewBinding.viewPager.currentItem != FAVOURITES_SCREEN_POSITION) {
      viewBinding.viewPager.setCurrentItem(FAVOURITES_SCREEN_POSITION, true)
    }
  }

  private fun onFabClick() {
    // scroll up
  }

  private fun switchTextStyles(selectedPosition: Int) {
    with(viewBinding) {
      if (selectedPosition == STOCK_SCREEN_POSITION) {
        setAsSelected(textViewHintStocks)
        setAsDefault(textViewHintFavourite)
        textViewHintStocks
      } else {
        setAsSelected(textViewHintFavourite)
        setAsDefault(textViewHintStocks)
        textViewHintFavourite
      }
      viewModel.lastSelectedPage = selectedPosition
    }
  }

  private fun setAsDefault(target: TextView) {
    TextViewCompat.setTextAppearance(target, R.style.textViewH2Shadowed)
  }

  private fun setAsSelected(target: TextView) {
    TextViewCompat.setTextAppearance(target, R.style.textViewH1)
  }
}

internal const val STOCK_SCREEN_POSITION = 0
internal const val FAVOURITES_SCREEN_POSITION = 1