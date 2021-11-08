package com.zentao.publish.service.history

import com.zentao.publish.condition.HistoryPageCondition
import com.zentao.publish.service.IMapService
import com.zentao.publish.viewmodel.History
import com.zentao.publish.viewmodel.PageResult

interface IHistoryService : IMapService {
    fun create(history: History): String

    fun update(history: History)

    fun delete(id: String)

    fun getAll(): List<History>

    fun getById(id: String): History?

    fun getByProduct(productId: String): List<History>

    fun getByProject(projectId: String): List<History>

    fun getPage(condition: HistoryPageCondition): PageResult<History>
}