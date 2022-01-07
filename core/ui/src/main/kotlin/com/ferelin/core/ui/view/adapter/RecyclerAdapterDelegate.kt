package com.ferelin.core.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

fun <VB : ViewBinding> createRecyclerAdapter(
  itemsViewType: Int,
  inflater: (LayoutInflater, ViewGroup?, Boolean) -> VB,
  onBind: (VB, ViewDataType, Int, MutableList<Any>) -> Unit
) = object : RecyclerAdapterDelegate(itemsViewType) {
  override fun onCreateViewHolder(parent: ViewGroup): BaseRecyclerViewHolder {
    return BaseViewHolder(
      viewBinding = inflater(LayoutInflater.from(parent.context), parent, false),
      bind = onBind
    )
  }
}

abstract class RecyclerAdapterDelegate(
  val itemsViewType: Int,
  val data: MutableList<ViewDataType> = mutableListOf()
) {
  abstract fun onCreateViewHolder(parent: ViewGroup): BaseRecyclerViewHolder
}