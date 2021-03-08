package com.ferelin.stockprice.ui.search

sealed class SearchSectionVisibility {
    object SHOW_HINTS_HIDE_RESULTS : SearchSectionVisibility()
    object HIDE_HINTS_SHOW_RESULTS : SearchSectionVisibility()
}