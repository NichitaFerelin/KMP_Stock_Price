package com.ferelin.stockprice.ui.previewSection.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.R
import com.ferelin.stockprice.ui.MainActivity
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment
import com.ferelin.stockprice.utils.StockPriceTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WelcomeFragment : Fragment() {

    private val mViewModel: WelcomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                StockPriceTheme {
                    Welcome(mViewModel)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            mViewModel.actionMoveToNextScreen.collect {
                if (it) {
                    (activity as MainActivity).dataInteractor.setFirstTimeLaunchState(false)
                    parentFragmentManager.commit {
                        replace(R.id.fragmentContainer, StocksPagerFragment())
                    }
                }
            }
        }
    }
}
