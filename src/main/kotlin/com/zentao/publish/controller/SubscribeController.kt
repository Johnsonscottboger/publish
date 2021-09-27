package com.zentao.publish.controller

import com.zentao.publish.service.subscribe.ISubscribeService
import com.zentao.publish.viewmodel.Subscribe
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("api/subscribe")
class SubscribeController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var _service: ISubscribeService

    @ResponseBody
    @PostMapping("/create", produces = ["application/json"])
    fun create(@RequestBody subscribe: Subscribe): String {
        return _service.create(subscribe)
    }

    @ResponseBody
    @PostMapping("/update")
    fun update(@RequestBody subscribe: Subscribe) {
        _service.update(subscribe)
    }

    @ResponseBody
    @PostMapping("/delete")
    fun delete(@RequestBody id: String) {
        _service.delete(id)
    }

    @ResponseBody
    @GetMapping("/{id}")
    fun get(@PathVariable id: String) : Subscribe? {
        return _service.getById(id)
    }

    @ResponseBody
    @GetMapping("/product/{productId}")
    fun getByProduct(@PathVariable productId: String) : List<Subscribe> {
        return _service.getByProduct(productId)
    }

    @ResponseBody
    @GetMapping("/project/{projectId}")
    fun getByProject(@PathVariable projectId: String) : List<Subscribe> {
        return _service.getByProject(projectId)
    }
}