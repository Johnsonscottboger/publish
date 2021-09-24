package com.zentao.publish.controller

import com.zentao.publish.entity.PubUser
import com.zentao.publish.service.user.IUserService
import com.zentao.publish.viewmodel.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller()
@RequestMapping("api/user")
class UserController {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var _service : IUserService


    @ResponseBody
    @PostMapping("/create", produces = ["application/json"])
    fun create(@RequestBody user: User) : String {
        return _service.create(user)
    }

    @ResponseBody
    @PostMapping("/update")
    fun update(@RequestBody user: User) {
        _service.update(user)
    }

    @ResponseBody
    @PostMapping("delete")
    fun delete(@RequestBody id: String) {
        _service.delete(id)
    }

    @ResponseBody
    @GetMapping("/{id}")
    fun get(@PathVariable id: String) : User? {
        return _service.getById(id)
    }
}