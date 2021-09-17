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

package com.ferelin.remote.utils

// https://finnhub.io/docs/api/introduction

const val COMPANY_ACTUAL_PRICE = "company-quote-api"
const val COMPANY_NEWS = "company-news-api"
const val PRICE_CHANGES_HISTORY = "stock-candle-api"

const val FINNHUB_BASE_URL = "https://finnhub.io/api/v1/"

const val RESPONSE_OK = 200
const val RESPONSE_ERROR = 405
const val RESPONSE_LIMIT = 406
const val RESPONSE_NO_DATA = 407
const val RESPONSE_UNDEFINED = 408

const val RESPONSE_WEB_SOCKET_CLOSED = 1001

/*
* Authentication response codes.
* */
const val VERIFICATION_CODE_SENT = 201
const val VERIFICATION_COMPLETED = 202
const val VERIFICATION_TOO_MANY_REQUESTS = 409