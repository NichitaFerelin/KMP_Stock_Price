package com.ferelin.stockprice.ui.forecasts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentForecastsBinding

class ForecastsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentForecastsBinding.inflate(inflater, container, false).root
    }
}