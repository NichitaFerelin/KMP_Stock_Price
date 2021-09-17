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

package com.ferelin.firebase.auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthenticatorImpl @Inject constructor(
    private val mFirebaseAuth: FirebaseAuth
) : FirebaseAuthenticator {

    // User ID is used to complete verification
    private var mUserVerificationId: String? = null

    private var mAuthCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    override val userId: String?
        get() = mFirebaseAuth.uid

    override val isUserAuthenticated: Boolean
        get() = mFirebaseAuth.currentUser != null

    override fun tryToLogIn(
        holderActivity: Activity,
        phone: String
    ) = callbackFlow<AuthenticationResponse> {

        // Empty phone number causes exception
        if (phone.isEmpty()) {
            trySend(AuthenticationResponse.EmptyPhone)
            return@callbackFlow
        }

        mAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                mUserVerificationId = p0
                trySend(AuthenticationResponse.CodeSent)
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                mFirebaseAuth.signInWithCredential(p0).addOnCompleteListener { task ->
                    val response = if (task.isSuccessful) {
                        AuthenticationResponse.Complete
                    } else {
                        AuthenticationResponse.Error
                    }
                    trySend(response)
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                val response = if (p0 is FirebaseTooManyRequestsException) {
                    AuthenticationResponse.TooManyRequests
                } else {
                    AuthenticationResponse.Error
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

    override fun completeAuthentication(code: String) {
        mUserVerificationId?.let { userVerificationId ->
            val credential = PhoneAuthProvider.getCredential(userVerificationId, code)
            mAuthCallbacks?.onVerificationCompleted(credential)
        }
    }

    override fun logOut() {
        mFirebaseAuth.signOut()
    }
}