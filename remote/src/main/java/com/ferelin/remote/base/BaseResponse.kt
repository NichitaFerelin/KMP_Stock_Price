package com.ferelin.remote.base

/*
* Common response model for all network responses.
* */
class BaseResponse<T>(
    var responseCode: Int? = null,
    var additionalMessage: String? = null,
    var responseData: T? = null
)