package com.ferelin.local.json

import android.content.Context
import com.ferelin.local.model.Company
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class JsonAssetsReader(
    private val mContext: Context,
    private val mFileName: String,
) {
    private val mMoshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    // TODO
    fun readCompanies(): Flow<List<Company>> = flow {
        val json = mContext.assets.open(mFileName).bufferedReader().use { it.readText() }
        val type = Types.newParameterizedType(List::class.java, Company::class.java)
        val adapter: JsonAdapter<List<Company>> = mMoshi.adapter(type)
        val list = adapter.fromJson(json) ?: emptyList()
        emit(list)
    }.flowOn(Dispatchers.Default)
}