package com.ferelin.core

fun <T> List<T>.itemsNotIn(param: List<T>): List<T> {
  val itemsNotIn = mutableListOf<T>()
  this.forEach { item ->
    var exists = false
    for (paramItem in param) {
      if (paramItem == item) {
        exists = true
        break
      }
    }
    if (!exists) itemsNotIn.add(item)
  }
  return itemsNotIn
}