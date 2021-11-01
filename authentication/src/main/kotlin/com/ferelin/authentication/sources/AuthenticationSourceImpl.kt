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

package com.ferelin.authentication.sources

import android.app.Activity
import com.ferelin.domain.sources.AuthResponse
import com.ferelin.domain.sources.AuthenticationSource
import com.ferelin.shared.DispatchersProvider
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val dispatchersProvider: DispatchersProvider
) : AuthenticationSource {

    companion object {
        private const val codeRequiredSize = 6
        private const val authTimeout = 30L
    }

    // User ID is used to complete verification
    private var userVerificationId: String? = null

    private var authCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    override fun tryToLogIn(
        holderActivity: Activity,
        phone: String
    ): Flow<AuthResponse> = callbackFlow {
        Timber.d("try to log in (phone = $phone)")

        trySend(AuthResponse.PhoneProcessing)

        // Empty phone number causes exception
        if (phone.isEmpty()) {
            trySend(AuthResponse.EmptyPhone)
            return@callbackFlow
        }

        authCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                Timber.d("code sent")

                userVerificationId = p0
                trySend(AuthResponse.CodeSent)
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                Timber.d("on verification completed")

                trySend(AuthResponse.CodeProcessing)

                firebaseAuth.signInWithCredential(p0).addOnCompleteListener { task ->
                    val response = if (task.isSuccessful) {
                        AuthResponse.Complete
                    } else {
                        AuthResponse.Error
                    }
                    trySend(response)
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Timber.d("on verification failed (exception = $p0)")

                val response = if (p0 is FirebaseTooManyRequestsException) {
                    AuthResponse.TooManyRequests
                } else {
                    AuthResponse.Error
                }
                trySend(response)
            }
        }

        authCallbacks?.let { authCallbacks ->
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(authTimeout, TimeUnit.SECONDS)
                .setActivity(holderActivity)
                .setCallbacks(authCallbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
        awaitClose()
    }

    override suspend fun completeAuthentication(code: String): Unit =
        withContext(dispatchersProvider.IO) {
            Timber.d("complete authentication (code = $code)")

            userVerificationId?.let { userVerificationId ->
                val credential = PhoneAuthProvider.getCredential(userVerificationId, code)
                authCallbacks?.onVerificationCompleted(credential)
            }
        }

    override suspend fun logOut(): Unit =
        withContext(dispatchersProvider.IO) {
            Timber.d("log out")

            firebaseAuth.signOut()
        }

    override fun isUserAuthenticated(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun getUserToken(): String? {
        return firebaseAuth.uid
    }

    override fun getCodeRequiredSize(): Int {
        return codeRequiredSize
    }
}

