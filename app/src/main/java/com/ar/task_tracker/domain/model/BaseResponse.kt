package com.ar.task_tracker.domain.model

class BaseResponse<T> {
    var responseMessage: String? = ""
    var responseCode: String? = ""
    var data: T? = null
}