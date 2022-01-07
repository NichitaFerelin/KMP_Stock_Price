package com.ferelin.core.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.ferelin.core.ApplicationContext
import javax.inject.Inject

class PermissionManager @Inject constructor(
  @ApplicationContext private val context: Context
) {
  fun writeExternalStorage() : Boolean {
    return ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
  }
}