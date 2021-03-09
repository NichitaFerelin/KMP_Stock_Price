package com.ferelin.stockprice.ui.ideas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentIdeasBinding

class IdeasFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentIdeasBinding.inflate(inflater, container, false).root
    }
}