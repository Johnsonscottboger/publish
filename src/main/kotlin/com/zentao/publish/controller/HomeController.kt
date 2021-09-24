package com.zentao.publish.controller

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.lang.IllegalArgumentException
import java.util.*

@RestController
class HomeController {

    @ResponseBody
    @GetMapping("/", produces = ["application/json"])
    fun index() : String {
        log.info("Application Running...")
        return "index"
    }

    private val log = LoggerFactory.getLogger(this::class.java)
}