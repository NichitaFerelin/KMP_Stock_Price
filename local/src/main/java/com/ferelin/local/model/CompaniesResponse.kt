package com.ferelin.local.model

data class CompaniesResponse(val code: Int = CompaniesResponses.LOADED_FROM_DB, val data: List<Company>)