package com.ferelin.stockprice.ui.stocksSection.stocksPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentStocksPagerBinding
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import kotlinx.coroutines.flow.SharedFlow

class StocksPagerFragment :
    BaseFragment<FragmentStocksPagerBinding, StocksPagerViewModel, StocksPagerViewController>() {

    override val mViewController: StocksPagerViewController = StocksPagerViewController()
    override val mViewModel: StocksPagerViewModel by viewModels {
        DataViewModelFactory(mCoroutineContext, mDataInteractor)
    }

    /*
    * Used by child fragments to detect fab clicks.
    * */
    val eventOnFabClicked: SharedFlow<Unit>
        get() = mViewController.eventOnFabClicked


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewBinding = FragmentStocksPagerBinding.inflate(inflater, container, false)
        mViewController.viewBinding = viewBinding
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewController.setUpArgumentsViewDependsOn(
            viewPagerAdapter = StocksPagerAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle
            )
        )
        setUpClickListeners()
        setUpBackPressedCallback()
    }

    private fun setUpClickListeners() {
        with(mViewController.viewBinding!!) {
            cardViewSearch.setOnClickListener { mViewController.onCardSearchClicked(this@StocksPagerFragment) }
            textViewHintStocks.setOnClickListener { mViewController.onHintStocksClicked() }
            textViewHintFavourite.setOnClickListener { mViewController.onHintFavouriteClicked() }
            fab.setOnClickListener { mViewController.onFabClicked() }
        }
    }

    private fun setUpBackPressedCallback() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!mViewController.handleOnBackPressed()) {
                        this.remove()
                        activity?.onBackPressed()
                    }
                }
            })
    }
}