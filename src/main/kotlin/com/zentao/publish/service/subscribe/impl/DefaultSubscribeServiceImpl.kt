package com.zentao.publish.service.subscribe.impl

import com.zentao.publish.dao.ISubscribeDao
import com.zentao.publish.entity.PubSubscribe
import com.zentao.publish.service.subscribe.ISubscribeService
import com.zentao.publish.viewmodel.Subscribe
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.Resource

@Service
class DefaultSubscribeServiceImpl : ISubscribeService {

    @Resource
    private lateinit var _dao: ISubscribeDao

    override fun create(subscribe: Subscribe): String {
        val subscribes = getAll()
        if (subscribes.any { p -> p.productId == subscribe.productId && p.projectId == subscribe.projectId })
            throw IllegalArgumentException("订阅已存在")
        return map(subscribe, PubSubscribe::class)?.run {
            id = UUID.randomUUID().toString()
            createTime = Date()
            _dao.create(this)
            id
        } ?: throw TypeCastException()
    }

    override fun update(subscribe: Subscribe) {
        val subscribes = getAll()
        if (subscribes.any { p -> p.id != subscribe.id && p.productId == subscribe.productId && p.projectId == subscribe.projectId })
            throw IllegalArgumentException("订阅已存在")
        map(subscribe, PubSubscribe::class)?.run {
            modifyTime = Date()
            _dao.update(this)
        }
    }

    override fun delete(id: String) {
        _dao.delete(id)
    }

    override fun getAll(): List<Subscribe> {
        val entities = _dao.getAll()
        return map(entities, Subscribe::class)
    }

    override fun getById(id: String): Subscribe? {
        return map(_dao.getById(id), Subscribe::class)
    }

    override fun getByProduct(productId: String): List<Subscribe> {
        val entities = _dao.getByProduct(productId)
        return map(entities, Subscribe::class)
    }

    override fun getByProject(projectId: String): List<Subscribe> {
        val entities = _dao.getByProject(projectId)
        return map(entities, Subscribe::class)
    }
}