package com.zentao.publish.controller

import com.zentao.publish.condition.HistoryPageCondition
import com.zentao.publish.service.history.IHistoryService
import com.zentao.publish.viewmodel.History
import com.zentao.publish.viewmodel.PageResult
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Api("历史记录服务")
@Controller
@RequestMapping("api/history")
class HistoryController {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var _service: IHistoryService

    @ResponseBody
    @ApiOperation("查询历史记录")
    @GetMapping("/{id}")
    fun get(@PathVariable id: String): History? {
        return _service.getById(id)
    }

    @ResponseBody
    @ApiOperation("查询产品历史记录")
    @GetMapping("/product/{productId}")
    fun getByProduct(@PathVariable productId: String): List<History> {
        return _service.getByProduct(productId)
    }

    @ResponseBody
    @ApiOperation("查询项目历史记录")
    @GetMapping("/project/{projectId}")
    fun getByProject(@PathVariable projectId: String): List<History> {
        return _service.getByProject(projectId)
    }

    @ResponseBody
    @ApiOperation("所有历史记录")
    @GetMapping("/all")
    fun getAll(): List<History> {
        return _service.getAll()
    }

    @ResponseBody
    @ApiOperation("分页查询历史记录")
    @PostMapping("/page")
    fun getPage(@RequestBody condition: HistoryPageCondition): PageResult<History> {
        return _service.getPage(condition)
    }
}