package com.ferelin.stockprice.ui.stocksSection.stocksPager

import android.animation.AnimatorInflater
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import com.google.android.material.transition.Hold
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StocksPagerFragment(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : Fragment() {

    private lateinit var mBinding: FragmentStocksPagerBinding

    private val mEventOnFabClicked = MutableSharedFlow<Unit>()
    val eventOnFabClicked: SharedFlow<Unit>
        get() = mEventOnFabClicked

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mBinding.viewPager.currentItem == 1) {
                    mBinding.viewPager.setCurrentItem(0, true)
                } else {
                    this.remove()
                    activity?.onBackPressed()
                }
            }
        })
    }

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
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }


        setUpComponents()
    }


    private fun setUpComponents() {
        mBinding.viewPager.apply {
            adapter = StocksPagerAdapter(childFragmentManager, lifecycle)

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position == 0) {
                        TextViewCompat.setTextAppearance(
                            mBinding.textViewHintStocks,
                            R.style.textViewH1
                        )
                        TextViewCompat.setTextAppearance(
                            mBinding.textViewHintFavourite,
                            R.style.textViewH2Shadowed
                        )
                        val animation =
                            AnimatorInflater.loadAnimator(requireContext(), R.animator.scale_in_out)
                        animation.setTarget(mBinding.textViewHintStocks)
                        animation.start()
                    } else {
                        TextViewCompat.setTextAppearance(
                            mBinding.textViewHintStocks,
                            R.style.textViewH2Shadowed
                        )
                        TextViewCompat.setTextAppearance(
                            mBinding.textViewHintFavourite,
                            R.style.textViewH1
                        )
                        val animation =
                            AnimatorInflater.loadAnimator(requireContext(), R.animator.scale_in_out)
                        animation.setTarget(mBinding.textViewHintFavourite)
                        animation.start()
                    }
                }
            })
        }

        mBinding.cardViewSearch.setOnClickListener {
            parentFragmentManager.commit {
                /*setCustomAnimations(
                    R.anim.scale_in_y,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.scale_out
                )*/
                setReorderingAllowed(true)
                replace(R.id.fragmentContainer, SearchFragment())
                addToBackStack(null)
                addSharedElement(mBinding.toolbar, "searchContainer")
            }
        }

        mBinding.textViewHintStocks.setOnClickListener {
            if (mBinding.viewPager.currentItem != 0) {
                mBinding.viewPager.setCurrentItem(0, true)
            }
        }

        mBinding.textViewHintFavourite.setOnClickListener {
            if (mBinding.viewPager.currentItem != 1) {
                mBinding.viewPager.setCurrentItem(1, true)
            }
        }

        mBinding.fab.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
                mEventOnFabClicked.emit(Unit)
                withContext(mCoroutineContext.Main) {
                    val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_out_2)
                    mBinding.fab.startAnimation(anim)
                    anim.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {

                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            mBinding.fab.visibility = View.INVISIBLE
                            mBinding.fab.scaleX = 1.0F
                            mBinding.fab.scaleY = 1.0F
                        }

                        override fun onAnimationRepeat(animation: Animation?) {
                        }
                    })
                }
            }
        }
    }
}