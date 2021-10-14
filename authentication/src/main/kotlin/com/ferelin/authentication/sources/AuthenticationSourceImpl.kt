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
import com.ferelin.domain.sources.AuthenticationSource
import com.ferelin.domain.sources.AuthResponse
import com.ferelin.shared.DispatchersProvider
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationSourceImpl @Inject constructor(
    private val mFirebaseAuth: FirebaseAuth,
    private val mDispatchersProvider: DispatchersProvider
) : AuthenticationSource {

    private companion object {
        const val sCodeRequiredSize = 6
    }

    // User ID is used to complete verification
    private var mUserVerificationId: String? = null

    private var mAuthCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    override fun tryToLogIn(
        holderActivity: Activity,
        phone: String
    ) = callbackFlow<AuthResponse> {
        Timber.d("try to log in (phone = $phone)")

        trySend(AuthResponse.PhoneProcessing)

        // Empty phone number causes exception
        if (phone.isEmpty()) {
            trySend(AuthResponse.EmptyPhone)
            return@callbackFlow
        }

        mAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                Timber.d("code sent")
                mUserVerificationId = p0
                trySend(AuthResponse.CodeSent)
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                Timber.d("on verification completed")

                trySend(AuthResponse.CodeProcessing)

                mFirebaseAuth.signInWithCredential(p0).addOnCompleteListener { task ->
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

        mAuthCallbacks?.let { authCallbacks ->
            val options = PhoneAuthOptions.newBuilder(mFirebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(30L, TimeUnit.SECONDS)
                .setActivity(holderActivity)
                .setCallbacks(authCallbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
        awaitClose()
    }

    override suspend fun completeAuthentication(code: String): Unit =
        withContext(mDispatchersProvider.IO) {
            Timber.d("complete authentication (code = $code)")

            mUserVerificationId?.let { userVerificationId ->
                val credential = PhoneAuthProvider.getCredential(userVerificationId, code)
                mAuthCallbacks?.onVerificationCompleted(credential)
            }
        }

    override suspend fun logOut(): Unit =
        withContext(mDispatchersProvider.IO) {
            Timber.d("log out")
            mFirebaseAuth.signOut()
        }

    override fun isUserAuthenticated(): Boolean {
        return mFirebaseAuth.currentUser != null
    }

    override fun getUserToken(): String? {
        return mFirebaseAuth.uid
    }

    override fun getCodeRequiredSize(): Int {
        return sCodeRequiredSize
    }
}

