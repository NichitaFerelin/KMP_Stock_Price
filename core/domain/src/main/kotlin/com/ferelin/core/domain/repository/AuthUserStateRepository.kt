package com.ferelin.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthUserStateRepository {
  val userToken: Flow<String>
  val userAuthenticated: Flow<Boolean>
}