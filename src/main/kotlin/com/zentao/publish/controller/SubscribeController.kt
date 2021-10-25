package com.zentao.publish.controller

import com.zentao.publish.service.subscribe.ISubscribeService
import com.zentao.publish.viewmodel.Subscribe
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Api("发布订阅管理")
@Controller
@RequestMapping("api/subscribe")
class SubscribeController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var _service: ISubscribeService

    @ResponseBody
    @ApiOperation("创建订阅")
    @PostMapping("/create", produces = ["application/json"])
    fun create(@RequestBody subscribe: Subscribe): String {
        return _service.create(subscribe)
    }

    @ResponseBody
    @ApiOperation("修改订阅")
    @PostMapping("/update")
    fun update(@RequestBody subscribe: Subscribe) {
        _service.update(subscribe)
    }

    @ResponseBody
    @ApiOperation("删除订阅")
    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: String) {
        _service.delete(id)
    }

    @ResponseBody
    @ApiOperation("查询订阅")
    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Subscribe? {
        return _service.getById(id)
    }

    @ResponseBody
    @ApiOperation("查询产品订阅")
    @GetMapping("/product/{productId}")
    fun getByProduct(@PathVariable productId: String): List<Subscribe> {
        return _service.getByProduct(productId)
    }

    @ResponseBody
    @ApiOperation("查询项目订阅")
    @GetMapping("/project/{projectId}")
    fun getByProject(@PathVariable projectId: String): List<Subscribe> {
        return _service.getByProject(projectId)
    }

    @ResponseBody
    @ApiOperation("所有项目")
    @GetMapping("/all")
    fun getAll(): List<Subscribe> {
        return _service.getAll()
    }
}