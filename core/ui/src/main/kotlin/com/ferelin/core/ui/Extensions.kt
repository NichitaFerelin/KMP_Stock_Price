package com.ferelin.core.ui.viewData.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.*

private const val DATE_PATTERN = "dd MMM yyyy"

fun Long.toDateStr(): String {
    val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.ROOT)
    return dateFormat.format(Date(this)).filter { it != ',' }
}

fun Context.openUrl(url: String) : Result<Any> = runCatching {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    startActivity(intent)
}