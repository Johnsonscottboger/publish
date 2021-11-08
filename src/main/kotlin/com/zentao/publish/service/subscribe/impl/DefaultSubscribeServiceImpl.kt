package com.zentao.publish.service.subscribe.impl

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.zentao.publish.condition.SubscribePageCondition
import com.zentao.publish.dao.ISubscribeDao
import com.zentao.publish.entity.PubSubscribe
import com.zentao.publish.service.subscribe.ISubscribeService
import com.zentao.publish.viewmodel.PageResult
import com.zentao.publish.viewmodel.Subscribe
import com.zentao.publish.viewmodel.User
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

    override fun deleteByProduct(productId: String) {
        _dao.deleteByProduct(productId)
    }

    override fun deleteByProject(projectId: String) {
        _dao.deleteByProject(projectId)
    }

    override fun getPage(condition: SubscribePageCondition): PageResult<Subscribe> {
        PageHelper.startPage<User>(condition.page.pageIndex + 1, condition.page.pageSize, true, true, false)
        val entities = map(_dao.getPage(condition), Subscribe::class)
        val pageInfo = PageInfo(entities)
        return PageResult(pageInfo.total, pageInfo.list)
    }
}