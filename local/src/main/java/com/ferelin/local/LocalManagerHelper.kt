package com.ferelin.local

import android.content.Context
import com.ferelin.local.model.Company
import kotlinx.coroutines.flow.Flow

interface LocalManagerHelper {

    fun getData(context: Context): Flow<List<Company>>
    fun insertData(data: List<Company>)
    fun insert(company: Company)
}