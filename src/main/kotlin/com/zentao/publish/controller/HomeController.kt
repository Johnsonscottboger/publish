package com.zentao.publish.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.lang.IllegalArgumentException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class HomeController {


    @GetMapping("/")
    fun index(response: HttpServletResponse)  {
        log.info("Application Running...")
        response.sendRedirect("/doc.html")
    }

    private val log = LoggerFactory.getLogger(this::class.java)
}