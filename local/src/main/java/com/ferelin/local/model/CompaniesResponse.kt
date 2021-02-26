package com.ferelin.local.model

data class CompaniesResponse(val code: Int = Responses.LOADED_FROM_DB, val data: List<Company>)