package com.zentao.publish.service.subscribe

import com.zentao.publish.condition.SubscribePageCondition
import com.zentao.publish.service.IMapService
import com.zentao.publish.viewmodel.PageResult
import com.zentao.publish.viewmodel.Subscribe

interface ISubscribeService : IMapService {
    fun create(subscribe: Subscribe) : String

    fun update(subscribe: Subscribe)

    fun delete(id: String)

    fun getAll(): List<Subscribe>

    fun getById(id: String): Subscribe?

    fun getByProduct(productId: String): List<Subscribe>

    fun getByProject(projectId: String): List<Subscribe>

    fun deleteByProduct(productId: String)

    fun deleteByProject(projectId: String)

    fun getPage(condition: SubscribePageCondition): PageResult<Subscribe>

    fun publish(projectId: String, productId: String) : String
}