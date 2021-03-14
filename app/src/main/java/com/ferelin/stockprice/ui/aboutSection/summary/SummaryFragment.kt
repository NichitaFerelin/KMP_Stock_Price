package com.ferelin.stockprice.ui.aboutSection.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ferelin.stockprice.databinding.FragmentSummaryBinding

class SummaryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSummaryBinding.inflate(inflater, container, false).root
    }
}