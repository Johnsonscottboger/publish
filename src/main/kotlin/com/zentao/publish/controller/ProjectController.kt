package com.zentao.publish.controller

import com.zentao.publish.service.project.IProjectService
import com.zentao.publish.viewmodel.Project
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists

@Api("项目管理服务")
@Controller
@RequestMapping("api/project")
class ProjectController {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var _service: IProjectService

    @ResponseBody
    @ApiOperation("创建项目")
    @PostMapping("/create", produces = ["application/json"])
    fun create(@RequestBody project: Project): String {
        return _service.create(project)
    }

    @ResponseBody
    @ApiOperation("修改项目")
    @PostMapping("/update")
    fun update(@RequestBody project: Project) {
        _service.update(project)
    }

    @ResponseBody
    @ApiOperation("删除项目")
    @PostMapping("/delete")
    fun delete(@RequestBody id: String) {
        _service.delete(id)
    }

    @ResponseBody
    @ApiOperation("查询项目")
    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Project? {
        return _service.getById(id)
    }

    @ResponseBody
    @ApiOperation("查询用户所属项目")
    @GetMapping("/user/{userId}")
    fun getByUserId(@PathVariable userId: String): List<Project> {
        return _service.getByUserId(userId)
    }

    @ResponseBody
    @ApiOperation("所有项目")
    @GetMapping("/all")
    fun getAll() : List<Project> {
        return _service.getAll()
    }

    @ResponseBody
    @ApiOperation("上传部署控制表模板")
    @PostMapping("/upload/{projectId}", produces = ["application/json"])
    fun upload(
        @PathVariable @ApiParam("项目主键", required = true) projectId: String,
        @RequestParam @ApiParam("上线部署控制表文件", required = true, example = "文档中预留{提测日期}、{提测版本}、{部署说明}插槽.")file: MultipartFile
    ): String {
        if (file.isEmpty)
            throw IllegalArgumentException("file can not be empty")
        val fileName = file.originalFilename!!
        val project = _service.getById(projectId) ?: throw IllegalArgumentException("找不到指定的项目")
        val path = Path(System.getenv("appdata"), "publish", "project", project.name!!)
        if (path.notExists())
            path.createDirectories()
        val dest = File(path.toString(), fileName)
        if (dest.exists()) dest.delete()
        file.transferTo(dest)
        return "上传成功"
    }
}