package com.zentao.publish.controller

import com.zentao.publish.service.svn.ISvnService
import com.zentao.publish.viewmodel.SvnCommitInput
import com.zentao.publish.viewmodel.SvnList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/svn")
class SvnController {

    @Autowired
    private lateinit var _service: ISvnService

    @ResponseBody
    @GetMapping("list/{projectId}")
    fun list(@PathVariable projectId: String): List<SvnList> {
        return _service.list(projectId)
    }

    @ResponseBody
    @GetMapping("version/{projectId}", produces = ["application/json"])
    fun version(@PathVariable projectId: String): String {
        return _service.version(projectId)
    }

    @ResponseBody
    @GetMapping("create/{projectId}", produces = ["application/json"])
    fun create(@PathVariable projectId: String): String {
        return _service.create(projectId)
    }

    @ResponseBody
    @PostMapping("commit", produces = ["application/json"])
    fun commit(@RequestBody input: SvnCommitInput): String {
        return _service.commit(input)
    }
}