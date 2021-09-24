package com.zentao.publish.service.project.impl

import com.zentao.publish.dao.IProjectDao
import com.zentao.publish.entity.PubProject
import com.zentao.publish.service.project.IProjectService
import com.zentao.publish.viewmodel.Project
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.Resource

@Service
class DefaultProjectService : IProjectService{

    @Resource
    private lateinit var _dao : IProjectDao

    override fun create(project: Project) : String {
        return map(project, PubProject::class)?.run {
            id = UUID.randomUUID().toString()
            createTime = Date()
            _dao.create(this)
            id
        } ?: throw TypeCastException()
    }

    override fun update(project: Project) {
        map(project, PubProject::class)?.run {
            modifyTime = Date()
            _dao.update(this)
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