package com.zentao.publish.interceptor

import com.zentao.publish.viewmodel.ResponseResult
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest
import kotlin.Exception

@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ResponseBody
    @ExceptionHandler(Exception::class, Error::class)
    fun exceptionHandler(request: HttpServletRequest, exception: Exception): ResponseResult {
        log.error(exception.message, exception)
        return ResponseResult.fail(exception.message!!)
    }
}