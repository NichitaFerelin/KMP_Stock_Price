package com.ferelin.stockprice.ui.stocksSection.stocksPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.R
import com.ferelin.stockprice.databinding.FragmentStocksPagerBinding
import com.ferelin.stockprice.ui.stocksSection.search.SearchFragment
import com.ferelin.stockprice.utils.AnimationManager
import com.google.android.material.transition.Hold
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StocksPagerFragment(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : Fragment() {

    private var mBinding: FragmentStocksPagerBinding? = null
    private val mViewHelper = StocksPagerViewHelper()

    private lateinit var mViewPagerChangeCallback: ViewPager2.OnPageChangeCallback

    private val mEventOnFabClicked = MutableSharedFlow<Unit>()
    val eventOnFabClicked: SharedFlow<Unit>
        get() = mEventOnFabClicked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStocksPagerBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeTransition(view)
        setUpComponents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding!!.viewPager.unregisterOnPageChangeCallback(mViewPagerChangeCallback)
        mBinding = null
    }

    private fun setUpViewPager() {
        mBinding!!.viewPager.apply {
            adapter = StocksPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
            mViewPagerChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    switchTextViewsStyle(position)
                }
            }
            registerOnPageChangeCallback(mViewPagerChangeCallback)
        }
    }

    private fun setUpComponents() {
        setUpViewPager()
        setUpBackPressedCallback()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mViewHelper.prepare(requireContext())

            mBinding!!.apply {
                cardViewSearch.setOnClickListener {
                    viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
                        parentFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace(R.id.fragmentContainer, SearchFragment())
                            addToBackStack(null)
                            addSharedElement(
                                mBinding!!.toolbar,
                                resources.getString(R.string.transitionSearchFragment)
                            )
                        }
                    }
                }
                textViewHintStocks.setOnClickListener {
                    if (viewPager.currentItem != 0) {
                        viewPager.setCurrentItem(0, true)
                    }
                }
                textViewHintFavourite.setOnClickListener {
                    if (viewPager.currentItem != 1) {
                        viewPager.setCurrentItem(1, true)
                    }
                }
                fab.setOnClickListener {
                    viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
                        mEventOnFabClicked.emit(Unit)
                        withContext(mCoroutineContext.Main) {
                            hideFab()
                        }
                    }
                }
            }
        }
    }

    private fun switchTextViewsStyle(position: Int) {
        if (position == 0) {
            TextViewCompat.setTextAppearance(
                mBinding!!.textViewHintStocks,
                R.style.textViewH1
            )
            TextViewCompat.setTextAppearance(
                mBinding!!.textViewHintFavourite,
                R.style.textViewH2Shadowed
            )
            mViewHelper.runScaleInOut(mBinding!!.textViewHintStocks)
        } else {
            TextViewCompat.setTextAppearance(
                mBinding!!.textViewHintStocks,
                R.style.textViewH2Shadowed
            )
            TextViewCompat.setTextAppearance(
                mBinding!!.textViewHintFavourite,
                R.style.textViewH1
            )
            mViewHelper.runScaleInOut(mBinding!!.textViewHintFavourite)
        }
    }

    private fun hideFab() {
        mViewHelper.runScaleOut(mBinding!!.fab, object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                mBinding!!.fab.visibility = View.INVISIBLE
                mBinding!!.fab.scaleX = 1.0F
                mBinding!!.fab.scaleY = 1.0F
            }
        })
    }

    private fun postponeTransition(view: View) {
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun setUpBackPressedCallback() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (mBinding?.viewPager?.currentItem == 1) {
                        mBinding?.viewPager?.setCurrentItem(0, true)
                    } else {
                        this.remove()
                        activity?.onBackPressed()
                    }
                }
            })
    }
}