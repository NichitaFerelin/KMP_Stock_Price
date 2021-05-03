package com.ferelin.remote.base


/**
 * [BaseResponse] for all networks responses.
 */
class BaseResponse<T>(
    var responseCode: Int? = null,
    var additionalMessage: String? = null,
    var responseData: T? = null
)