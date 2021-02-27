package com.ferelin.local.json

import com.ferelin.local.model.Company
import kotlinx.coroutines.flow.Flow

interface JsonManagerHelper {
    fun getCompaniesFromJson(): Flow<List<Company>>
}