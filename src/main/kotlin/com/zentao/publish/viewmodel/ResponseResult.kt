package com.zentao.publish.viewmodel

data class ResponseResult(
        var state: Int,
        var message: String,
        var content: Any? = null,
) {
    companion object {
        private const val STATE_SUCCESS = 2000
        private const val STATE_FAILURE = 5000

        fun succ(data: Any? = null): ResponseResult {
            return ResponseResult(STATE_SUCCESS, "ok", data)
        }

        fun fail(message: String, data: Any? = null) : ResponseResult {
            return ResponseResult(STATE_FAILURE, message, data)
        }
    }
}