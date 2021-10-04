package com.zentao.publish.controller

import com.zentao.publish.service.project.IProjectService
import com.zentao.publish.viewmodel.Project
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists

@Controller
@RequestMapping("api/project")
class ProjectController {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var _service: IProjectService

    @ResponseBody
    @PostMapping("/create", produces = ["application/json"])
    fun create(@RequestBody project: Project): String {
        return _service.create(project)
    }

    @ResponseBody
    @PostMapping("/update")
    fun update(@RequestBody project: Project) {
        _service.update(project)
    }

    @ResponseBody
    @PostMapping("/delete")
    fun delete(@RequestBody id: String) {
        _service.delete(id)
    }

    @ResponseBody
    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Project? {
        return _service.getById(id)
    }

    @ResponseBody
    @GetMapping("/user/{userId}")
    fun getByUserId(@PathVariable userId: String): List<Project> {
        return _service.getByUserId(userId)
    }

    @ResponseBody
    @PostMapping("/upload/{projectId}", produces = ["application/json"])
    fun upload(@PathVariable projectId: String, @RequestParam file: MultipartFile): String {
        if (file.isEmpty)
            throw IllegalArgumentException("file can not be empty")
        val fileName = file.originalFilename!!
        val project = _service.getById(projectId) ?: throw IllegalArgumentException("找不到指定的项目")
        val path = Path(System.getenv("appdata"), "publish", "project", project.name!!)
        if (path.notExists())
            path.createDirectories()
        val dest = File(path.toString(), fileName)
        if(dest.exists()) dest.delete()
        file.transferTo(dest)
        return "上传成功"
    }
}