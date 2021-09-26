package com.zentao.publish.service.product.impl

import com.zentao.publish.dao.IProductDao
import com.zentao.publish.entity.PubProduct
import com.zentao.publish.service.product.IProductService
import com.zentao.publish.viewmodel.Product
import com.zentao.publish.viewmodel.Project
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.Resource

@Service
class DefaultProductServiceImpl : IProductService {

    @Resource
    private lateinit var _dao: IProductDao

    override fun create(product: Product): String {
        return map(product, PubProduct::class)?.run {
            id = UUID.randomUUID().toString()
            createTime = Date()
            _dao.create(this)
            id
        } ?: throw TypeCastException()
    }

    override fun update(product: Product) {
        map(product, PubProduct::class)?.run {
            modifyTime = Date()
            _dao.update(this)
        }
    }

    override fun delete(id: String) {
        _dao.delete(id)
    }

    override fun getAll(): List<Product> {
        val entities = _dao.getAll()
        return map(entities, Product::class)
    }

    override fun getById(id: String): Product? {
        return map(_dao.getById(id), Product::class)
    }
}