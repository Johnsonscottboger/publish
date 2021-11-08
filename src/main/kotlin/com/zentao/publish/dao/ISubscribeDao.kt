package com.zentao.publish.dao

import com.zentao.publish.condition.SubscribePageCondition
import com.zentao.publish.entity.PubSubscribe
import com.zentao.publish.viewmodel.Project

interface ISubscribeDao {
    fun create(entity: PubSubscribe)

    fun update(entity: PubSubscribe)

    fun delete(id: String)

    fun getAll(): List<PubSubscribe>

    fun getById(id: String): PubSubscribe?

    fun getByProduct(productId: String): List<PubSubscribe>

    fun getByProject(projectId: String): List<PubSubscribe>

    fun deleteByProduct(productId: String)

    fun deleteByProject(projectId: String)

    fun getPage(condition: SubscribePageCondition): List<PubSubscribe>
}