package com.ferelin.stockprice.ui.stocksSection.favourite

/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.stockprice.databinding.FragmentFavouriteBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewAnimator
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewController
import com.ferelin.stockprice.ui.stocksSection.common.StocksRecyclerAdapter

class FavouriteViewController : BaseStocksViewController<FragmentFavouriteBinding>() {

    override val mViewAnimator: BaseStocksViewAnimator = BaseStocksViewAnimator()

    override val mStocksRecyclerView: RecyclerView
        get() = viewBinding.recyclerViewFavourites

    override fun onDestroyView() {
        postponeReferencesRemove {
            mStocksRecyclerView.adapter = null
            super.onDestroyView()
        }
    }

    fun onNewItem() {
        mStocksRecyclerView.scrollToPosition(0)
    }

    fun setArgumentsViewDependsOn(
        stocksRecyclerAdapter: StocksRecyclerAdapter,
        fragmentManager: FragmentManager
    ) {
        super.fragmentManager = fragmentManager
        mStocksRecyclerView.adapter = stocksRecyclerAdapter
    }
}