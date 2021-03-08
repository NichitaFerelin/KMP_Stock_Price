package com.ferelin.local.databases.companies

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class CompaniesConverter {

    private val mMoshi = Moshi.Builder().build()

    @TypeConverter
    fun listStringToJson(data: List<String>): String {
        val type = Types.newParameterizedType(List::class.java, String::class.javaObjectType)
        val adapter = mMoshi.adapter<List<String>>(type)
        return adapter.toJson(data)
    }

    @TypeConverter
    fun jsonToListString(json: String): List<String> {
        val type = Types.newParameterizedType(List::class.java, String::class.javaObjectType)
        val adapter = mMoshi.adapter<List<String>>(type)
        return adapter.fromJson(json)!!
    }
}