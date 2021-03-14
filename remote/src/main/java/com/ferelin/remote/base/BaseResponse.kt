package com.ferelin.remote.base

class BaseResponse<T>(
    var responseCode: Int? = null,
    var additionalMessage: String? = null,
    var responseData: T? = null
)