package com.zentao.publish.dao

import com.zentao.publish.condition.ProjectPageCondition
import com.zentao.publish.entity.PubProject

interface IProjectDao {

    fun create(entity: PubProject)

    fun update(entity: PubProject)

    fun delete(id: String)

    fun getAll(): List<PubProject>

    fun getById(id: String): PubProject?

    fun getByUserId(userId: String): List<PubProject>

    fun getPage(condition: ProjectPageCondition): List<PubProject>
}