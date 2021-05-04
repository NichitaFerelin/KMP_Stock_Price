package com.ferelin.stockprice.ui.previewSection.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentLoadingBinding
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoadingFragment :
    BaseFragment<FragmentLoadingBinding, LoadingViewModel, LoadingViewController>() {

    override val mViewController: LoadingViewController = LoadingViewController()
    override val mViewModel: LoadingViewModel by viewModels {
        DataViewModelFactory(mCoroutineContext, mDataInteractor)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewBinding = FragmentLoadingBinding.inflate(inflater, container, false)
        mViewController.viewBinding = viewBinding
        return viewBinding.root
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            collectStateFirstTimeLaunch()
        }
    }

    private suspend fun collectStateFirstTimeLaunch() {
        mViewModel.isFirstTimeLaunchState.collect { isFirstTimeLaunch ->
            mViewController.onFirstTimeStateChanged(this, isFirstTimeLaunch)
        }
    }
}