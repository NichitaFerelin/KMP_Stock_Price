package com.ferelin.stockprice.ui.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentSummaryBinding

class SummaryFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSummaryBinding.inflate(inflater, container, false).root
    }
}