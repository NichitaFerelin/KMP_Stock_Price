package com.ferelin.stockprice.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentNewsBinding

class NewsFragment: BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentNewsBinding.inflate(inflater, container, false).root
    }
}