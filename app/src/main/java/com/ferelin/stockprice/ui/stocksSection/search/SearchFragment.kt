package com.ferelin.stockprice.ui.stocksSection.search

import android.content.res.Configuration
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
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.R
import com.ferelin.stockprice.databinding.FragmentSearchBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
import com.ferelin.stockprice.ui.stocksSection.search.itemDecoration.SearchItemDecoration
import com.ferelin.stockprice.ui.stocksSection.search.itemDecoration.SearchItemDecorationLandscape
import com.ferelin.stockprice.utils.anim.AnimationManager
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

    override var mStocksRecyclerView: RecyclerView? = null
    private var mBinding: FragmentSearchBinding? = null

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
        mBinding = FragmentSearchBinding.inflate(inflater, container, false).also {
            mStocksRecyclerView = it.recyclerViewSearchResults
        }
        return mBinding!!.root
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)

        mViewModel.recyclerAdapter.setHeader(resources.getString(R.string.hintStocks))
        restoreTransitionState()
        setUpBackPressedCallback()
        setUpRecyclerViews()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mFragmentManager = parentFragmentManager
            mBinding!!.imageViewBack.setOnClickListener {
                activity?.onBackPressed()
            }
            mBinding!!.editTextSearch.doAfterTextChanged {
                Timer().apply {
                    schedule(timerTask {
                        mViewModel.onSearchTextChanged(it?.toString() ?: "")
                    }, 200)
                }
            }
            mBinding!!.imageViewIconClose.setOnClickListener {
                mBinding!!.editTextSearch.setText("")
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
                            mBinding!!.root.transitionToStart()
                            mViewModel.onTransition(0)
                        } else {
                            mBinding!!.root.transitionToEnd()
                            mViewModel.onTransition(1)
                        }
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
                            mBinding!!.editTextSearch.requestFocus()
                            openKeyboard(requireContext(), mBinding!!.editTextSearch)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.postponeReferencesRemove {
            mBinding?.recyclerViewSearchedHistory?.adapter = null
            mBinding?.recyclerViewPopularRequests?.adapter = null
            mBinding?.recyclerViewSearchResults?.adapter = null
            mBinding = null
        }
    }

    private fun restoreTransitionState() {
        if (mViewModel.transitionState == 1) {
            mBinding!!.root.progress = 1F
            mBinding!!.imageViewIconClose.visibility = View.VISIBLE
        }
    }

    private fun switchCloseIconVisibility(hideIcon: Boolean) {
        mBinding!!.apply {
            if (hideIcon && imageViewIconClose.visibility != View.GONE) {
                mViewHelper.runScaleOut(imageViewIconClose, object : AnimationManager() {
                    override fun onAnimationEnd(animation: Animation?) {
                        imageViewIconClose.visibility = View.INVISIBLE
                    }
                })
            } else if (!hideIcon && imageViewIconClose.visibility != View.VISIBLE) {
                mViewHelper.runScaleIn(imageViewIconClose, object : AnimationManager() {
                    override fun onAnimationStart(animation: Animation?) {
                        imageViewIconClose.visibility = View.VISIBLE
                    }
                })
            }
        }
    }

    private fun onSearchTickerClicked(item: AdaptiveSearchRequest) {
        mBinding!!.editTextSearch.setText(item.searchText)
        mBinding!!.editTextSearch.setSelection(item.searchText.length)
    }

    private fun setUpBackPressedCallback() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when {
                        mBinding != null && mViewModel.lastSearchText.isNotEmpty() -> {
                            mBinding!!.editTextSearch.setText("")
                        }
                        else -> {
                            this.remove()
                            hideKeyboard(requireContext(), mBinding!!.root)
                            activity?.onBackPressed()
                        }
                    }
                }
            })
    }

    private fun setUpRecyclerViews() {
        mBinding!!.recyclerViewSearchedHistory.apply {
            addItemDecoration(getItemDecoration())
            adapter = mViewModel.searchRequestAdapter.also {
                it.setOnTickerClickListener { item, _ ->
                    onSearchTickerClicked(item)
                }
            }
        }
        mBinding!!.recyclerViewPopularRequests.apply {
            addItemDecoration(getItemDecoration())
            adapter = mViewModel.popularRequestsAdapter.also {
                it.setOnTickerClickListener { item, _ ->
                    onSearchTickerClicked(item)
                }
            }
        }
    }

    private fun getItemDecoration(): SearchItemDecoration {
        return if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            SearchItemDecorationLandscape(requireContext())
        } else {
            SearchItemDecoration(requireContext())
        }
    }
}