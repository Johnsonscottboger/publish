package com.zentao.publish.service.project.impl

import com.zentao.publish.dao.IProjectDao
import com.zentao.publish.entity.PubProject
import com.zentao.publish.service.project.IProjectService
import com.zentao.publish.service.subscribe.ISubscribeService
import com.zentao.publish.viewmodel.Project
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.Resource

@Service
class DefaultProjectServiceImpl : IProjectService {

    @Resource
    private lateinit var _dao: IProjectDao

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
                _subscribeService.create(subscribe)
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
}