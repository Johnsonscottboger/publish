package com.zentao.publish.controller

import com.zentao.publish.service.user.IUserService
import com.zentao.publish.viewmodel.User
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Api("用户管理服务")
@Controller()
@RequestMapping("api/user")
class UserController {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var _service : IUserService


    @ResponseBody
    @ApiOperation("创建用户")
    @PostMapping("/create", produces = ["application/json"])
    fun create(@RequestBody user: User) : String {
        return _service.create(user)
    }

    @ResponseBody
    @ApiOperation("修改用户")
    @PostMapping("/update")
    fun update(@RequestBody user: User) {
        _service.update(user)
    }

    @ResponseBody
    @ApiOperation("删除用户")
    @PostMapping("delete")
    fun delete(@RequestBody id: String) {
        _service.delete(id)
    }

    @ResponseBody
    @ApiOperation("查询用户")
    @GetMapping("/{id}")
    fun get(@PathVariable id: String) : User? {
        val user = _service.getById(id)
        return user?.copy(password = "******")
    }
}