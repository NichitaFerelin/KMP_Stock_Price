package com.ferelin.stockprice.ui.stocksSection.stocks

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
import com.ferelin.stockprice.databinding.FragmentStocksBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
import com.ferelin.stockprice.ui.stocksSection.common.StocksItemDecoration
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment
import com.ferelin.stockprice.utils.showSnackbar
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StocksFragment : BaseStocksFragment<StocksViewModel>() {

    private lateinit var mBinding: FragmentStocksBinding

    override val mViewModel: StocksViewModel by viewModels {
        DataViewModelFactory(CoroutineContextProvider(), mDataInteractor)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStocksBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun setUpViewComponents() {
        super.setUpViewComponents()

        mFragmentManager = requireParentFragment().parentFragmentManager

        mBinding.recyclerViewStocks.apply {
            adapter = mViewModel.recyclerAdapter
            addItemDecoration(StocksItemDecoration(requireContext()))
            setHasFixedSize(true)
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch {
                (requireParentFragment() as StocksPagerFragment).eventOnFabClicked.collect {
                    withContext(mCoroutineContext.Main) {

                        if((mBinding.recyclerViewStocks.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 40) {
                            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_out)
                            val animEnd =
                                AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in)
                            animEnd.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation?) {
                                    mBinding.recyclerViewStocks.visibility = View.VISIBLE
                                    mBinding.recyclerViewStocks.smoothScrollToPosition(0)
                                }

                                override fun onAnimationEnd(animation: Animation?) {
                                }

                                override fun onAnimationRepeat(animation: Animation?) {
                                }

                            })


                            mBinding.recyclerViewStocks.startAnimation(anim)
                            anim.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationEnd(animation: Animation?) {
                                    mBinding.recyclerViewStocks.visibility = View.GONE
                                    mBinding.recyclerViewStocks.scrollToPosition(20)
                                    mBinding.recyclerViewStocks.startAnimation(animEnd)
                                    //mBinding.recyclerViewStocks.y += mBinding.recyclerViewStocks.height
                                    //mBinding.recyclerViewStocks.startAnimation(animEnd)
                                }

                                override fun onAnimationRepeat(animation: Animation?) {
                                }

                                override fun onAnimationStart(animation: Animation?) {
                                    mBinding.recyclerViewStocks.smoothScrollBy(
                                        0,
                                        (-mBinding.recyclerViewStocks.height).toInt()
                                    )
                                }
                            })
                            /*mBinding.recyclerViewStocks.smoothScrollBy(
                                0,
                                (-mBinding.recyclerViewStocks.height / 1.2).toInt()
                            )*/

                            //
                        } else {
                            mBinding.recyclerViewStocks.smoothScrollToPosition(0)
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
}