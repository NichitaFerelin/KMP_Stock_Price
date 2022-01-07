package com.ferelin.features.stocks.data.entity.crypto

import android.content.Context
import com.ferelin.core.ApplicationContext
import com.ferelin.core.checkBackgroundThread
import com.ferelin.features.stocks.data.mapper.CryptoMapper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javax.inject.Inject

internal interface CryptoJsonSource {
  fun parseJson(): List<CryptoDBO>
}

internal class CryptoJsonSourceImpl @Inject constructor(
  @ApplicationContext private val context: Context
) : CryptoJsonSource {
  private val moshi by lazy(LazyThreadSafetyMode.NONE) {
    Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  }

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