package com.zentao.publish.dao

import com.zentao.publish.condition.ProductPageCondition
import com.zentao.publish.entity.PubProduct
import com.zentao.publish.entity.PubProject

interface IProductDao {

    fun create(entity: PubProduct)

    fun update(entity: PubProduct)

    fun delete(id: String)

    fun getAll(): List<PubProduct>

    fun getById(id: String): PubProduct?

    fun getPage(productPageCondition: ProductPageCondition): List<PubProduct>
}