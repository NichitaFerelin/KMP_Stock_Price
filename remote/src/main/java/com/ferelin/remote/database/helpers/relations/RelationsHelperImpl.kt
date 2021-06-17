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

package com.ferelin.remote.database.helpers.relations

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.database.RealtimeValueEventListener
import com.ferelin.remote.utils.Api
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RelationsHelperImpl @Inject constructor(
    private val mDatabaseFirebase: DatabaseReference
) : RelationsHelper {

    companion object {
        private const val sRelationsRef = "relations"
    }

    override fun addNewRelation(
        sourceUserLogin: String,
        secondSideUserLogin: String,
        relationId: String
    ) {
        mDatabaseFirebase
            .child(sRelationsRef)
            .child(sourceUserLogin)
            .child(relationId)
            .setValue(secondSideUserLogin)
    }

    override fun getUserRelations(userLogin: String) =
        callbackFlow<BaseResponse<List<Pair<Int, String>>>> {
            mDatabaseFirebase
                .child(sRelationsRef)
                .child(userLogin)
                .addValueEventListener(object : RealtimeValueEventListener() {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val relations = mutableListOf<Pair<Int, String>>()
                            for (relationSnapshot in snapshot.children) {
                                val associatedUserLogin = relationSnapshot.value?.toString() ?: ""
                                val relationId = relationSnapshot.key?.toInt() ?: 0
                                relations.add(Pair(relationId, associatedUserLogin))
                            }
                            trySend(
                                BaseResponse(
                                    responseCode = Api.RESPONSE_OK,
                                    additionalMessage = userLogin,
                                    responseData = relations.toList()
                                )
                            )
                        } else trySend(BaseResponse(Api.RESPONSE_NO_DATA))
                    }
                })
            awaitClose()
        }
}