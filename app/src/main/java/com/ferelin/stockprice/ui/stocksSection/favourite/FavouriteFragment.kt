package com.ferelin.stockprice.ui.stocksSection.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.R
import com.ferelin.stockprice.databinding.FragmentFavouriteBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
import com.ferelin.stockprice.ui.stocksSection.common.StocksItemDecoration
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@FlowPreview
class FavouriteFragment : BaseStocksFragment<FavouriteViewModel>() {

    private lateinit var mBinding: FragmentFavouriteBinding

    override val mViewModel: FavouriteViewModel by viewModels {
        DataViewModelFactory(CoroutineContextProvider(), mDataInteractor)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun setUpViewComponents() {
        super.setUpViewComponents()

        mFragmentManager = requireParentFragment().parentFragmentManager

        mBinding.recyclerViewFavourites.apply {
            adapter = mViewModel.recyclerAdapter
            addItemDecoration(StocksItemDecoration(requireContext()))
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch {
                mViewModel.actionScrollToTop.collect {
                    withContext(mCoroutineContext.Main) {
                        scrollToTop()
                    }

                }
            }
            launch {
                (requireParentFragment() as StocksPagerFragment).eventOnFabClicked.collect {
                    withContext(mCoroutineContext.Main) {

                            if ((mBinding.recyclerViewFavourites.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 40) {
                                val anim =
                                    AnimationUtils.loadAnimation(requireContext(), R.anim.scale_out)
                                val animEnd =
                                    AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in)
                                animEnd.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationStart(animation: Animation?) {
                                        mBinding.recyclerViewFavourites.visibility = View.VISIBLE
                                        mBinding.recyclerViewFavourites.smoothScrollToPosition(0)
                                    }

                                    override fun onAnimationEnd(animation: Animation?) {
                                    }

                                    override fun onAnimationRepeat(animation: Animation?) {
                                    }

                                })


                                mBinding.recyclerViewFavourites.startAnimation(anim)
                                anim.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationEnd(animation: Animation?) {
                                        mBinding.recyclerViewFavourites.visibility = View.GONE
                                        mBinding.recyclerViewFavourites.scrollToPosition(20)
                                        mBinding.recyclerViewFavourites.startAnimation(animEnd)
                                        //mBinding.recyclerViewFavourites.y += mBinding.recyclerViewFavourites.height
                                        //mBinding.recyclerViewFavourites.startAnimation(animEnd)
                                    }

                                    override fun onAnimationRepeat(animation: Animation?) {
                                    }

                                    override fun onAnimationStart(animation: Animation?) {
                                        mBinding.recyclerViewFavourites.smoothScrollBy(
                                            0,
                                            (-mBinding.recyclerViewFavourites.height).toInt()
                                        )
                                    }
                                })
                                /*mBinding.recyclerViewFavourites.smoothScrollBy(
                                    0,
                                    (-mBinding.recyclerViewFavourites.height / 1.2).toInt()
                                )*/

                                //
                            } else {
                                mBinding.recyclerViewFavourites.smoothScrollToPosition(0)
                            }
                    }
                }
            }
        }
    }

    private fun scrollToTop() {
        mBinding.recyclerViewFavourites.scrollToPosition(0)
    }
}
