package com.zentao.publish.dao

import com.zentao.publish.condition.HistoryPageCondition
import com.zentao.publish.entity.PubHistory
import com.zentao.publish.entity.PubProject

interface IHistoryDao {

    fun create(entity: PubHistory)

    fun update(entity: PubHistory)

    fun delete(id: String)

    fun getAll(): List<PubHistory>

    fun getById(id: String): PubHistory?

    fun getByProduct(productId: String): List<PubHistory>

    fun getByProject(projectId: String): List<PubProject>

    fun getPage(condition: HistoryPageCondition): List<PubHistory>
}