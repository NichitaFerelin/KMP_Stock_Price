package com.ferelin.stockprice.ui.stocksSection.search

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.R
import com.ferelin.stockprice.databinding.FragmentSearchBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
import com.ferelin.stockprice.ui.stocksSection.common.StocksItemDecoration
import com.ferelin.stockprice.utils.AnimationManager
import com.ferelin.stockprice.utils.hideKeyboard
import com.ferelin.stockprice.utils.openKeyboard
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.timerTask

class SearchFragment : BaseStocksFragment<SearchViewModel, SearchViewHelper>() {

    override val mViewHelper: SearchViewHelper = SearchViewHelper()
    override val mViewModel: SearchViewModel by viewModels {
        DataViewModelFactory(CoroutineContextProvider(), mDataInteractor)
    }

    private lateinit var mBinding: FragmentSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)

        mViewModel.recyclerAdapter.setTextDividers(hashMapOf(0 to resources.getString(R.string.hintStocks)))
        restoreTransitionState()
        setUpBackPressedCallback()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mFragmentManager = parentFragmentManager
            setUpRecyclerViews()

            mBinding.imageViewBack.setOnClickListener {
                activity?.onBackPressed()
            }
            mBinding.editTextSearch.doAfterTextChanged {
                Timer().apply {
                    schedule(timerTask {
                        mViewModel.onSearchTextChanged(it?.toString() ?: "")
                    }, 200)
                }
            }
            mBinding.imageViewIconClose.setOnClickListener {
                mBinding.editTextSearch.setText("")
            }
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch {
                mViewModel.actionShowHintsHideResults.collect {
                    withContext(mCoroutineContext.Main) {
                        if (it) {
                            mBinding.root.transitionToStart()
                        } else mBinding.root.transitionToEnd()
                        mViewModel.onTransition()
                    }
                }
            }
            launch {
                mViewModel.actionHideCloseIcon.collect {
                    withContext(mCoroutineContext.Main) {
                        switchCloseIconVisibility(it)
                    }
                }
            }
            launch {
                mViewModel.actionShowError.collect {
                    withContext(mCoroutineContext.Main) {
                        showToast(it)
                    }
                }
            }
            launch {
                mViewModel.actionShowKeyboard.collect {
                    if (it) {
                        delay(300)
                        mViewModel.onOpenKeyboard()
                        withContext(mCoroutineContext.Main) {
                            mBinding.editTextSearch.requestFocus()
                            openKeyboard(requireContext(), mBinding.editTextSearch)
                        }
                    }
                }
            }
        }
    }

    private fun restoreTransitionState() {
        if (mViewModel.transitionState == 1) {
            mBinding.root.progress = 1F
            mBinding.imageViewIconClose.visibility = View.VISIBLE
        }
    }

    private fun switchCloseIconVisibility(hideIcon: Boolean) {
        if (hideIcon && mBinding.imageViewIconClose.visibility != View.GONE) {
            mViewHelper.runScaleOut(mBinding.imageViewIconClose, object : AnimationManager() {
                override fun onAnimationEnd(animation: Animation?) {
                    mBinding.imageViewIconClose.visibility = View.INVISIBLE
                }
            })
        } else if (!hideIcon && mBinding.imageViewIconClose.visibility != View.VISIBLE) {
            mViewHelper.runScaleIn(mBinding.imageViewIconClose, object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    mBinding.imageViewIconClose.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun onSearchTickerClicked(item: AdaptiveSearchRequest) {
        mBinding.editTextSearch.setText(item.searchText)
        mBinding.editTextSearch.setSelection(item.searchText.length)
    }

    private fun setUpBackPressedCallback() {
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    this@SearchFragment::mBinding.isInitialized && mViewModel.lastSearchText.isNotEmpty() -> {
                        mBinding.editTextSearch.setText("")
                    }
                    else -> {
                        this.remove()
                        hideKeyboard(requireContext(), mBinding.root)
                        activity?.onBackPressed()
                    }
                }
            }
        })
    }

    private fun setUpRecyclerViews() {
        mBinding.recyclerViewSearchResults.apply {
            addItemDecoration(StocksItemDecoration(requireContext()))
            adapter = mViewModel.recyclerAdapter
        }
        mBinding.recyclerViewSearchedHistory.apply {
            addItemDecoration(SearchItemDecoration(requireContext()))
            adapter = mViewModel.searchesAdapter.also {
                it.setOnTickerClickListener { item, _ ->
                    onSearchTickerClicked(item)
                }
            }
        }
        mBinding.recyclerViewPopularRequests.apply {
            addItemDecoration(SearchItemDecoration(requireContext()))
            adapter = mViewModel.popularRequestsAdapter.also {
                it.setPopularSearches()
                it.setOnTickerClickListener { item, _ ->
                    onSearchTickerClicked(item)
                }
            }
        }
    }
}