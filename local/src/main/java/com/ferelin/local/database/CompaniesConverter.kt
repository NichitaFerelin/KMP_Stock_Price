package com.ferelin.local.database

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
/*
* Room data converter
* */
class CompaniesConverter {

    private val mMoshi = Moshi.Builder().build()

    private val mTypeListString =
        Types.newParameterizedType(List::class.java, String::class.javaObjectType)
    private val mTypeStringList =
        Types.newParameterizedType(List::class.java, String::class.javaObjectType)

    @TypeConverter
    fun listStringToJson(data: List<String>): String {
        val adapter = mMoshi.adapter<List<String>>(mTypeListString)
        return adapter.toJson(data)
    }

    @TypeConverter
    fun jsonToListString(json: String): List<String> {
        val adapter = mMoshi.adapter<List<String>>(mTypeStringList)
        return adapter.fromJson(json) ?: emptyList()
    }
}