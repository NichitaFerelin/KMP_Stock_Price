package com.ferelin.core

fun <T> List<T>.itemsNotIn(param: List<T>): List<T> {
  val itemsNotIn = mutableListOf<T>()
  this.forEach { item ->
    if (item !in param) {
      itemsNotIn.add(item)
    }
  }
  return itemsNotIn
}