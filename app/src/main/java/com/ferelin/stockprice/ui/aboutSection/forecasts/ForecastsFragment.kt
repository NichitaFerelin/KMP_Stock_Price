package com.ferelin.stockprice.ui.aboutSection.forecasts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ferelin.stockprice.databinding.FragmentForecastsBinding

class ForecastsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentForecastsBinding.inflate(inflater, container, false).root
    }
}