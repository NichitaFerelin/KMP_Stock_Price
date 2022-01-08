package com.ferelin.features.stocks.ui.main

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.core.ui.R
import com.ferelin.core.ui.view.adapter.createRecyclerAdapter
import com.ferelin.features.stocks.databinding.ItemCryptoBinding
import com.ferelin.features.stocks.ui.defaults.StocksFragment
import com.ferelin.features.stocks.ui.favourites.FavouriteStocksFragment

internal fun createCryptoAdapter() = createRecyclerAdapter(
  CRYPTO_VIEW_TYPE,
  ItemCryptoBinding::inflate
) { viewBinding, item, _, _ ->
  with(viewBinding) {
    item as CryptoViewData
    textViewCryptoName.text = item.name
    textViewCryptoPrice.text = item.price
    textViewCryptoProfit.text = item.profit
    textViewCryptoProfit.setTextColor(
      ContextCompat.getColor(viewBinding.root.context, item.profitColor)
    )

    Glide
      .with(root)
      .load(item.logoUrl)
      .transition(DrawableTransitionOptions.withCrossFade())
      .error(
        AppCompatResources.getDrawable(
          viewBinding.root.context,
          R.drawable.ic_load_error_20
        )
      )
      .into(imageViewCrypto)
  }
}

internal const val CRYPTO_VIEW_TYPE = 0

internal class CryptoItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
  private val margin = context.resources.getDimension(R.dimen.cryptoItemOffset).toInt()

  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    super.getItemOffsets(outRect, view, parent, state)
    if (parent.getChildAdapterPosition(view) == 0) {
      outRect.left = margin
    }
    outRect.right = margin
  }
}

internal class StocksPagerAdapter(
  fm: FragmentManager,
  lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {
  override fun getItemCount(): Int = 2

  override fun createFragment(position: Int): Fragment {
    return when (position) {
      0 -> StocksFragment()
      1 -> FavouriteStocksFragment()
      else -> error("No fragment for position: $position")
    }
  }
}