package com.ferelin.stockprice.ui.aboutSection.ideas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ferelin.stockprice.databinding.FragmentIdeasBinding

class IdeasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentIdeasBinding.inflate(inflater, container, false).root
    }
}