package com.ferelin.stockprice.androidApp.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthUserStateRepository {
  val userToken: Flow<String>
  val userAuthenticated: Flow<Boolean>
}