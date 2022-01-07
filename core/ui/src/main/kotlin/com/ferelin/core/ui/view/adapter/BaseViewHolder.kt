package com.ferelin.core.ui.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class ViewDataType(val itemViewType: Int) {
  abstract fun getUniqueId(): Long
}

abstract class BaseRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  abstract fun bind(viewData: ViewDataType, position: Int, payloads: MutableList<Any>)
}

open class BaseViewHolder<VB : ViewBinding>(
  val viewBinding: VB,
  private val bind: (VB, ViewDataType, Int, MutableList<Any>) -> Unit
) : BaseRecyclerViewHolder(viewBinding.root) {
  override fun bind(viewData: ViewDataType, position: Int, payloads: MutableList<Any>) {
    bind.invoke(viewBinding, viewData, position, payloads)
  }
}