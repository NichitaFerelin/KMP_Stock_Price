package com.ferelin.stockprice.ui.stocksSection.search

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.R
import com.ferelin.stockprice.databinding.FragmentSearchBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
import com.ferelin.stockprice.ui.stocksSection.common.StocksItemDecoration
import com.ferelin.stockprice.utils.showSnackbar
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.timerTask

class SearchFragment : BaseStocksFragment<SearchViewModel>() {

    private lateinit var mBinding: FragmentSearchBinding

    override val mViewModel: SearchViewModel by viewModels {
        DataViewModelFactory(CoroutineContextProvider(), mDataInteractor)
    }

    private var progress: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mViewModel.lastSearchText.isEmpty()) {
                    this.remove()
                    activity?.onBackPressed()
                } else {
                    mBinding.editTextSearch.setText("")
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progress = savedInstanceState?.getInt(stateSave) ?: 0

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

    override fun setUpViewComponents() {
        super.setUpViewComponents()

        mViewModel.recyclerAdapter.setTextDividers(hashMapOf(0 to "Stocks")) // todo

        mFragmentManager = parentFragmentManager

        with(mBinding) {

            imageViewBack.setOnClickListener {
                activity?.onBackPressed()
            }

            recyclerViewSearchResults.apply {
                addItemDecoration(StocksItemDecoration(requireContext()))
                adapter = mViewModel.recyclerAdapter
            }
            recyclerViewSearchedHistory.apply {
                adapter = mViewModel.searchesAdapter.also {
                    it.setOnTickerClickListener { item, _ ->
                        onSearchTickerClicked(item)
                    }
                }
                addItemDecoration(SearchItemDecoration(requireContext()))
            }
            recyclerViewPopularRequests.apply {
                adapter = mViewModel.popularRequestsAdapter.also {
                    it.setPopularSearches()
                    it.setOnTickerClickListener { item, _ ->
                        onSearchTickerClicked(item)
                    }
                }
                addItemDecoration(SearchItemDecoration(requireContext()))
            }
            editTextSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    Timer().apply {
                        schedule(timerTask {
                            mViewModel.onSearchTextChanged(s?.toString() ?: "")
                        }, 200)
                    }
                }
            })


            imageViewIconClose.setOnClickListener {
                mBinding.editTextSearch.setText("")
            }


            // TODO передать savedInstanceState чтобы не показывать клавиатуру просто так
            viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
                delay(900)
                withContext(mCoroutineContext.Main) {
                    mBinding.editTextSearch.requestFocus()
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(mBinding.editTextSearch, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }
    }

    companion object {
        const val stateSave = "stateSave"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(stateSave, progress)
    }

    override fun initObservers() {
        super.initObservers()

        Log.d("Test", "$progress")
        if (progress == 1) {
            mBinding.root.progress = 1F
            mBinding.imageViewIconClose.visibility = View.VISIBLE
        }

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch {
                mViewModel.actionShowHintsHideResults.collect {
                    withContext(mCoroutineContext.Main) {
                        if (it) {
                            mBinding.root.transitionToStart()
                            progress = 1
                            /*mBinding.sectionResults.visibility = View.GONE
                            mBinding.sectionHints.visibility = View.VISIBLE*/
                        } else {
                            mBinding.root.transitionToEnd()
                            progress = 1
                            /*mBinding.sectionHints.visibility = View.GONE
                            mBinding.sectionResults.visibility = View.VISIBLE*/
                        }
                    }
                }

            }
            launch {
                mViewModel.actionHideCloseIcon.collect {
                    withContext(mCoroutineContext.Main) {
                        if (it && mBinding.imageViewIconClose.visibility != View.GONE) {
                            val animator = AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.scale_out_right_top
                            )
                            animator.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation?) {
                                }

                                override fun onAnimationEnd(animation: Animation?) {
                                    mBinding.imageViewIconClose.visibility = View.INVISIBLE
                                }

                                override fun onAnimationRepeat(animation: Animation?) {
                                }
                            })
                            mBinding.imageViewIconClose.startAnimation(animator)

                        } else if (!it && mBinding.imageViewIconClose.visibility != View.VISIBLE) {
                            val animator = AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.scale_in_right_top
                            )
                            animator.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation?) {
                                    mBinding.imageViewIconClose.visibility = View.VISIBLE

                                }

                                override fun onAnimationEnd(animation: Animation?) {

                                }

                                override fun onAnimationRepeat(animation: Animation?) {
                                }

                            })
                            mBinding.imageViewIconClose.startAnimation(animator)

                        }
                    }
                }
            }
            launch {
                mViewModel.actionShowError.collect {
                    withContext(mCoroutineContext.Main) {
                        showSnackbar(mBinding.root, it)
                    }
                }
            }
        }
    }

    private fun onSearchTickerClicked(item: AdaptiveSearchRequest) {
        mBinding.editTextSearch.setText(item.searchText)
        mBinding.editTextSearch.setSelection(item.searchText.length)
    }
}