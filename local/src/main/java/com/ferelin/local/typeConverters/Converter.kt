package com.ferelin.local.typeConverters

/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

internal class Converter {

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