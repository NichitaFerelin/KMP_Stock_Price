package com.ferelin.core.data.entity.crypto

import android.content.Context
import com.ferelin.core.checkBackgroundThread
import com.ferelin.core.data.mapper.CryptoMapper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

internal interface CryptoJsonSource {
  fun parseJson(): List<CryptoDBO>
}

internal class CryptoJsonSourceImpl @Inject constructor(
  private val context: Context,
  private val moshi: Moshi
) : CryptoJsonSource {
  override fun parseJson(): List<CryptoDBO> {
    checkBackgroundThread()
    val type = Types.newParameterizedType(List::class.java, CryptoJson::class.java)
    val json = context.assets
      .open(CRYPTO_JSON_FILE)
      .bufferedReader()
      .use { it.readText() }

    val adapter = moshi.adapter<List<CryptoJson>?>(type)
    val parsedItems = adapter.fromJson(json)!!
    return CryptoMapper.map(parsedItems)
  }
}

internal data class CryptoJson(
  val symbol: String,
  val name: String,
  val logo_url: String
)

internal const val CRYPTO_JSON_FILE = "crypto.json"