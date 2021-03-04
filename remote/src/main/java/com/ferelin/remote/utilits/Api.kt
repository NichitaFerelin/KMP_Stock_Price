package com.ferelin.remote.utilits

object Api {
    // https://finnhub.io/docs/api/introduction
    const val FINNHUB_BASE_URL = "https://finnhub.io/api/v1/"
    const val FINNHUB_TOKEN = "c0toc6v48v6qoe9bl7ug"

    const val RESPONSE_OK = 200
    const val RESPONSE_LIMIT = 429
    const val RESPONSE_NO_DATA = 490
    const val RESPONSE_UNDEFINED = 491

    const val RESPONSE_WEB_SOCKET_CLOSE = 492
    const val RESPONSE_TRADE_NOT_AVAILABLE = 493

    // TODO CHECK code without internet
}