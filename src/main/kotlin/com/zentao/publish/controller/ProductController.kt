package com.zentao.publish.controller

import com.zentao.publish.service.product.IProductService
import com.zentao.publish.viewmodel.Product
import com.zentao.publish.viewmodel.Project
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("api/product")
class ProductController {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var _service : IProductService

    @ResponseBody
    @PostMapping("/create", produces = ["application/json"])
    fun create(@RequestBody product: Product) : String {
        return _service.create(product)
    }

    @ResponseBody
    @PostMapping("/update")
    fun update(@RequestBody product: Product) {
        _service.update(product)
    }

    @ResponseBody
    @PostMapping("/delete")
    fun delete(@RequestBody id: String) {
        _service.delete(id)
    }

    @ResponseBody
    @GetMapping("/{id}")
    fun get(@PathVariable id: String) : Product? {
        return _service.getById(id)
    }
}