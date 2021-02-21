package com.ferelin.local.database

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class CompaniesConverter {

    private val mMoshi = Moshi.Builder().build()

    @TypeConverter
    fun listDoubleToJson(data: List<Double>): String {
        val type = Types.newParameterizedType(List::class.java, Double::class.javaObjectType)
        val adapter = mMoshi.adapter<List<Double>>(type)
        return adapter.toJson(data)
    }

    @TypeConverter
    fun jsonToListDouble(json: String): List<Double> {
        val type = Types.newParameterizedType(List::class.java, Double::class.javaObjectType)
        val adapter = mMoshi.adapter<List<Double>>(type)
        return adapter.fromJson(json)!!
    }

    @TypeConverter
    fun listLongToJson(data: List<Long>): String {
        val type = Types.newParameterizedType(List::class.java, Long::class.javaObjectType)
        val adapter = mMoshi.adapter<List<Long>>(type)
        return adapter.toJson(data)
    }

    @TypeConverter
    fun jsonToListLong(json: String): List<Long> {
        val type = Types.newParameterizedType(List::class.java, Long::class.javaObjectType)
        val adapter = mMoshi.adapter<List<Long>>(type)
        return adapter.fromJson(json)!!
    }
}