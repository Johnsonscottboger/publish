package com.zentao.publish.controller

import com.zentao.publish.service.product.IProductService
import com.zentao.publish.viewmodel.Product
import com.zentao.publish.viewmodel.Project
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Api("产品管理服务")
@Controller
@RequestMapping("api/product")
class ProductController {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var _service : IProductService

    @ResponseBody
    @ApiOperation("创建产品")
    @PostMapping("/create", produces = ["application/json"])
    fun create(@RequestBody product: Product) : String {
        return _service.create(product)
    }

    @ResponseBody
    @ApiOperation("修改产品")
    @PostMapping("/update")
    fun update(@RequestBody product: Product) {
        _service.update(product)
    }

    @ResponseBody
    @ApiOperation("删除产品")
    @PostMapping("/delete")
    fun delete(@RequestBody id: String) {
        _service.delete(id)
    }

    @ResponseBody
    @ApiOperation("查询产品")
    @GetMapping("/{id}")
    fun get(@PathVariable id: String) : Product? {
        return _service.getById(id)
    }

    @ResponseBody
    @ApiOperation("所有产品")
    @GetMapping("/all")
    fun getAll() : List<Product> {
        return _service.getAll()
    }
}