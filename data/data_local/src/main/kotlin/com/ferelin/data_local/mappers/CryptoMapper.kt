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

package com.ferelin.data_local.mappers

import com.ferelin.data_local.entities.CryptoDBO
import com.ferelin.data_local.pojo.CryptoPojo
import com.ferelin.domain.entities.Crypto
import javax.inject.Inject

class CryptoMapper @Inject constructor() {

    fun map(crypto: Crypto): CryptoDBO {
        return CryptoDBO(
            id = crypto.id,
            name = crypto.name,
            symbol = crypto.symbol,
            logoUrl = crypto.logoUrl
        )
    }

    fun map(cryptoDBO: CryptoDBO): Crypto {
        return Crypto(
            id = cryptoDBO.id,
            symbol = cryptoDBO.symbol,
            name = cryptoDBO.name,
            logoUrl = cryptoDBO.logoUrl
        )
    }

    fun map(cryptoPojo: CryptoPojo, index: Int): Crypto {
        return Crypto(
            id = index,
            symbol = cryptoPojo.symbol,
            name = cryptoPojo.name,
            logoUrl = cryptoPojo.logo_url
        )
    }
}