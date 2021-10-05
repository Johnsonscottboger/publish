package com.zentao.publish.interceptor

import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer
import com.zentao.publish.viewmodel.ResponseResult
import org.apache.tomcat.util.json.JSONParser
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import springfox.documentation.swagger.web.UiConfiguration

@ControllerAdvice
class ResponseResultHandler : ResponseBodyAdvice<Any> {
    override fun supports(p0: MethodParameter, p1: Class<out HttpMessageConverter<*>>): Boolean {
        return p0.hasMethodAnnotation(ResponseBody::class.java)
    }

    override fun beforeBodyWrite(
        p0: Any?,
        p1: MethodParameter,
        p2: MediaType,
        p3: Class<out HttpMessageConverter<*>>,
        p4: ServerHttpRequest,
        p5: ServerHttpResponse
    ): Any? {
        if(p4.uri.path.contains("swagger") || p4.uri.path.contains("api-docs"))
            return p0
        if (p0 is ResponseResult)
            return p0
        if (p0 is Error)
            return ResponseResult.fail(p0.message!!, p0)
        return ResponseResult.succ(p0)
    }
}