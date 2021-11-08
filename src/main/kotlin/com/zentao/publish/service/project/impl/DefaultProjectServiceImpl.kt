package com.zentao.publish.service.project.impl

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.zentao.publish.condition.ProjectPageCondition
import com.zentao.publish.dao.IProjectDao
import com.zentao.publish.dao.ISubscribeDao
import com.zentao.publish.entity.PubProject
import com.zentao.publish.extensions.deleteRec
import com.zentao.publish.service.project.IProjectService
import com.zentao.publish.service.subscribe.ISubscribeService
import com.zentao.publish.viewmodel.PageResult
import com.zentao.publish.viewmodel.Project
import com.zentao.publish.viewmodel.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.util.*
import javax.annotation.Resource
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists

@Service
class DefaultProjectServiceImpl : IProjectService {

    @Value("\${publishpath}")
    private lateinit var _appdata: String

    @Resource
    private lateinit var _dao: IProjectDao

    @Resource
    private lateinit var _subscribeDao: ISubscribeDao

    @Resource
    private lateinit var _subscribeService: ISubscribeService

    override fun create(project: Project): String {
        val projects = getAll()
        if (projects.any { p -> p.name == project.name })
            throw IllegalArgumentException("项目已存在")
        if (projects.any { p -> p.publishPath!!.startsWith(project.publishPath!!.removeSuffix("/")) })
            throw IllegalArgumentException("项目已存在")
        val id = map(project, PubProject::class)?.run {
            id = UUID.randomUUID().toString()
            createTime = Date()
            _dao.create(this)
            id
        } ?: throw TypeCastException()

        project.subscribeList?.forEach { subscribe ->
            try {
                val entity = subscribe.copy(projectId = id)
                _subscribeService.create(entity)
            } catch (ex: Exception) {
                //ignore
            }
        }
        return id
    }

    override fun update(project: Project) {
        val projects = getAll()
        if (projects.any { p -> p.id != project.id && p.name == project.name })
            throw IllegalArgumentException("项目已存在")
        if (projects.any { p -> p.id != project.id && p.publishPath!!.startsWith(project.publishPath!!.removeSuffix("/")) })
            throw IllegalArgumentException("项目已存在")

        //更新的时候如果修改了项目名称, 将上线部署控制表移动到新名称中
        try {
            val oldProject = projects.find { p -> p.id == project.id }
            if (oldProject != null && oldProject.name != project.name) {
                val oldProjectPath = Path(_appdata, "publish", "project", oldProject.name!!)
                if (oldProjectPath.exists()) {
                    val templateFile = if (File(oldProjectPath.toString(), "上线部署控制表.doc").exists()) {
                        File(oldProjectPath.toString(), "上线部署控制表.doc")
                    } else if (File(oldProjectPath.toString(), "上线部署控制表.docx").exists()) {
                        File(oldProjectPath.toString(), "上线部署控制表.docx")
                    } else {
                        null
                    }

                    if (templateFile != null) {
                        val newProjectPath = Path(_appdata, "publish", "project", project.name!!)
                        if (!newProjectPath.exists()) newProjectPath.createDirectories()
                        templateFile.copyTo(File(newProjectPath.toString(), templateFile.name), true)
                    }
                }

                oldProjectPath.deleteRec()
            }
        } catch (error: Throwable) {

        }

        map(project, PubProject::class)?.run {
            modifyTime = Date()
            _dao.update(this)
        }

        val list = _subscribeService.getByProject(project.id!!).toMutableList()
        if (project.subscribeList != null) {
            project.subscribeList.forEach { subscribe ->
                val item = list.find { p -> p.id == subscribe.id }
                if (item != null) {
                    list.remove(item)
                    _subscribeService.update(subscribe)
                } else {
                    _subscribeService.create(subscribe)
                }
            }
            list.forEach { subscribe -> _subscribeService.delete(subscribe.id!!) }
        }
    }

    override fun delete(id: String) {
        _dao.delete(id)
        _subscribeDao.deleteByProject(id)
    }

    override fun getAll(): List<Project> {
        val entities = _dao.getAll()
        return map(entities, Project::class)
    }

    override fun getById(id: String): Project? {
        return _dao.getById(id)?.run {
            map(this, Project::class)
        }
    }

    override fun getByUserId(userId: String): List<Project> {
        return _dao.getByUserId(userId).run {
            map(this, Project::class)
        }
    }

    override fun getPage(condition: ProjectPageCondition): PageResult<Project> {
        PageHelper.startPage<User>(condition.page.pageIndex + 1, condition.page.pageSize, true, true, false)
        val entities = map(_dao.getPage(condition), Project::class)
        val pageInfo = PageInfo(entities)
        return PageResult(pageInfo.total, pageInfo.list)
    }
}
