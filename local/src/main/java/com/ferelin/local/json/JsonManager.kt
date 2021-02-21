package com.ferelin.local.json

import android.content.Context
import com.ferelin.local.model.Company
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class JsonManager(private val mContext : Context) : JsonManagerHelper {

    override fun getCompaniesFromJson(): Flow<List<Company>> = flow {
        val reader = JsonAssetsReader(mContext, JsonAssets.COMPANIES)
        reader.readCompanies().first {
            emit(it)
            true
        }
    }.flowOn(Dispatchers.IO)
}