package com.zentao.publish.service.project

import com.zentao.publish.condition.ProjectPageCondition
import com.zentao.publish.service.IMapService
import com.zentao.publish.viewmodel.PageResult
import com.zentao.publish.viewmodel.Project


interface IProjectService : IMapService{

    fun create(project: Project) : String

    fun update(project: Project)

    fun delete(id: String)

    fun getAll(): List<Project>

    fun getById(id: String): Project?

    fun getByUserId(userId: String): List<Project>

    fun getPage(condition: ProjectPageCondition): PageResult<Project>
}