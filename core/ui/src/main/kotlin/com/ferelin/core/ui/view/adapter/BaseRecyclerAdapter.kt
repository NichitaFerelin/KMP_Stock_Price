package com.ferelin.core.ui.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BaseRecyclerAdapter(
  vararg adapterDelegates: RecyclerAdapterDelegate
) : RecyclerView.Adapter<BaseRecyclerViewHolder>() {
  private var currentList = mutableListOf<ViewDataType>()
  private val delegates = adapterDelegates.toList()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
    val targetIndex = delegates.indexOfFirst { it.itemsViewType == viewType }
    return delegates[targetIndex].onCreateViewHolder(parent)
  }

  override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
    holder.bind(currentList[position], position, mutableListOf())
  }

  override fun onBindViewHolder(
    holder: BaseRecyclerViewHolder,
    position: Int,
    payloads: MutableList<Any>
  ) {
    if (payloads.isNotEmpty()) {
      holder.bind(currentList[position], position, payloads)
    } else {
      super.onBindViewHolder(holder, position, payloads)
    }
  }

  override fun getItemCount(): Int {
    return currentList.size
  }

  override fun getItemViewType(position: Int): Int {
    return currentList[position].itemViewType
  }

  override fun getItemId(position: Int): Long {
    return if (hasStableIds()) {
      currentList[position].getUniqueId()
    } else {
      super.getItemId(position)
    }
  }

  fun getByPosition(position: Int): ViewDataType {
    return currentList[position]
  }

  fun getPositionOf(selector: (ViewDataType) -> Boolean): Int {
    return currentList.indexOfFirst(selector)
  }

  fun update(viewDataType: ViewDataType, position: Int, payloads: Any? = null) {
    currentList[position] = viewDataType
    notifyItemChanged(position, payloads)
  }

  fun add(position: Int, viewDataType: ViewDataType) {
    currentList.add(position, viewDataType)
    notifyItemInserted(position)
  }

  fun removeAt(position: Int) {
    currentList.removeAt(position)
    notifyItemRemoved(position)
  }

  fun setData(data: List<ViewDataType>) {
    if (currentList.isNotEmpty()) {
      val itemCount = currentList.size
      currentList = mutableListOf()
      notifyItemRangeRemoved(0, itemCount)
    }
    currentList = data.toMutableList()
    notifyItemRangeInserted(0, currentList.size)
  }
}