package com.zentao.publish.service.product

import com.zentao.publish.condition.ProductPageCondition
import com.zentao.publish.service.IMapService
import com.zentao.publish.viewmodel.PageResult
import com.zentao.publish.viewmodel.Product
import com.zentao.publish.viewmodel.Project

interface IProductService : IMapService {

    fun create(product: Product): String

    fun update(product: Product)

    fun delete(id: String)

    fun getAll(): List<Product>

    fun getById(id: String): Product?

    fun getPage(condition: ProductPageCondition): PageResult<Product>
}