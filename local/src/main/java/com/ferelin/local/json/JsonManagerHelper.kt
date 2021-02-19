package com.ferelin.local.json

import android.content.Context
import com.ferelin.local.model.Company
import kotlinx.coroutines.flow.Flow

interface JsonManagerHelper {
    fun getData(context: Context): Flow<List<Company>>
}