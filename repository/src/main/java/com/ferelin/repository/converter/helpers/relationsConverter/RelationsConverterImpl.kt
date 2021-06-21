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

package com.ferelin.repository.converter.helpers.relationsConverter

import com.ferelin.local.models.Relation
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utils.Api
import com.ferelin.repository.adaptiveModels.AdaptiveRelation
import com.ferelin.repository.utils.RepositoryResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RelationsConverterImpl @Inject constructor() : RelationsConverter {

    override fun convertRelationForLocal(item: AdaptiveRelation): Relation {
        return Relation(
            id = item.id,
            associatedUserLogin = item.associatedUserLogin
        )
    }

    override fun convertLocalRelationResponseForUi(
        data: List<Relation>
    ): RepositoryResponse<List<AdaptiveRelation>> {
        return RepositoryResponse.Success(
            data = data.map { relation ->
                AdaptiveRelation(
                    id = relation.id,
                    associatedUserLogin = relation.associatedUserLogin
                )
            }
        )
    }

    override fun convertRealtimeRelationResponseForUi(
        response: BaseResponse<List<Pair<Int, String>>>?
    ): RepositoryResponse<List<AdaptiveRelation>> {
        return if (response != null && response.responseCode == Api.RESPONSE_OK) {
            RepositoryResponse.Success(
                data = response.responseData!!.map { pair ->
                    AdaptiveRelation(
                        id = pair.first,
                        associatedUserLogin = pair.second
                    )
                }
            )
        } else RepositoryResponse.Failed()
    }
}