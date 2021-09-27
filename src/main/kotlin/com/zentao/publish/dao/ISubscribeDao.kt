package com.zentao.publish.dao

import com.zentao.publish.entity.PubSubscribe

interface ISubscribeDao {
    fun create(entity: PubSubscribe)

    fun update(entity: PubSubscribe)

    fun delete(id: String)

    fun getAll(): List<PubSubscribe>

    fun getById(id: String): PubSubscribe?

    fun getByProduct(productId: String): List<PubSubscribe>

    fun getByProject(projectId: String): List<PubSubscribe>
}