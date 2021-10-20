package com.zentao.publish.controller

import com.zentao.publish.service.svn.ISvnService
import com.zentao.publish.viewmodel.SvnCommitInput
import com.zentao.publish.viewmodel.SvnList
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Api("SVN管理服务")
@Controller
@RequestMapping("/api/svn")
class SvnController {

    @Autowired
    private lateinit var _service: ISvnService

    @ResponseBody
    @ApiOperation("列出项目所有版本")
    @GetMapping("list/{projectId}")
    fun list(@PathVariable projectId: String): List<SvnList> {
        return _service.list(projectId)
    }

    @ResponseBody
    @ApiOperation("项目下一版本号")
    @GetMapping("version/{projectId}", produces = ["application/json"])
    fun version(@PathVariable projectId: String): String {
        return _service.version(projectId)
    }

    @ResponseBody
    @ApiOperation("创建项目版本")
    @GetMapping("create/{projectId}", produces = ["application/json"])
    fun create(@PathVariable projectId: String): String {
        return _service.create(projectId)
    }

    @ResponseBody
    @ApiOperation("提交指定版本")
    @PostMapping("commit", produces = ["application/json"])
    fun commit(@RequestBody input: SvnCommitInput): String {
        return _service.commit(input)
    }
}