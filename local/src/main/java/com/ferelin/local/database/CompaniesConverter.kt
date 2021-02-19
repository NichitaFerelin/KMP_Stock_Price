package com.ferelin.local.database

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class CompaniesConverter {

    private val mMoshi = Moshi.Builder().build()

    @TypeConverter
    fun toJson(data: List<Double>): String {
        val type = Types.newParameterizedType(List::class.java, Double::class.javaObjectType)
        val adapter = mMoshi.adapter<List<Double>>(type)
        return adapter.toJson(data)
    }

    @TypeConverter
    fun fromJson(json: String): List<Double> {
        val type = Types.newParameterizedType(List::class.java, Double::class.javaObjectType)
        val adapter = mMoshi.adapter<List<Double>>(type)
        return adapter.fromJson(json)!!
    }
}