package com.ferelin.local.json

import android.content.Context
import com.ferelin.local.model.Company
import kotlinx.coroutines.flow.Flow

class JsonManager(private val mContext: Context) : JsonManagerHelper {

    override fun getCompaniesFromJson(): Flow<List<Company>> {
        return JsonAssetsReader(mContext, JsonAssets.COMPANIES).readCompanies()
    }
}