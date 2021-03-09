package com.ferelin.stockprice.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentChartBinding

class ChartFragment : BaseFragment() {

    private lateinit var mBinding: FragmentChartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChartBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       /* arguments?.let {
            mBinding.textViewLastPrice.text = it[sCurrentPrice] as String
            mBinding.textViewPercents.text = it[sDayDelta] as String
        }

        mBinding.chartView.setOnTouchListener {
            val position = it.position
            mBinding.cardView.x = position.x
            mBinding.cardView.y = position.y
        }*/
    }

    companion object {

        private const val sCurrentPrice = "current_price"
        private const val sDayDelta = "day_delta"

        fun newInstance(currentPrice: String, dayDelta: String): ChartFragment {
            return ChartFragment().apply {
                arguments = bundleOf(
                    sCurrentPrice to "144.4",
                    sDayDelta to "444"
                )
            }
        }
    }
}