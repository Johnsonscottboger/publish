package com.zentao.publish.service.history.impl

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.zentao.publish.condition.HistoryPageCondition
import com.zentao.publish.dao.IHistoryDao
import com.zentao.publish.entity.PubHistory
import com.zentao.publish.service.history.IHistoryService
import com.zentao.publish.viewmodel.History
import com.zentao.publish.viewmodel.PageResult
import com.zentao.publish.viewmodel.User
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.Resource

@Service
class DefaultHistoryServiceImpl : IHistoryService {

    @Resource
    private lateinit var _dao: IHistoryDao

    override fun create(history: History): String {
        return map(history, PubHistory::class)?.run {
            id = UUID.randomUUID().toString()
            createTime = Date()
            _dao.create(this)
            id
        } ?: throw TypeCastException()
    }

    override fun update(history: History) {
        map(history, PubHistory::class)?.run {
            _dao.update(this)
        }
    }

    override fun delete(id: String) {
        _dao.delete(id)
    }

    override fun getAll(): List<History> {
        val entities = _dao.getAll()
        return map(entities, History::class)
    }

    override fun getById(id: String): History? {
        return map(_dao.getById(id), History::class)
    }

    override fun getByProduct(productId: String): List<History> {
        val entities = _dao.getByProduct(productId)
        return map(entities, History::class)
    }

    override fun getByProject(projectId: String): List<History> {
        val entities = _dao.getByProject(projectId)
        return map(entities, History::class)
    }

    override fun getPage(condition: HistoryPageCondition): PageResult<History> {
        PageHelper.startPage<User>(condition.page.pageIndex + 1, condition.page.pageSize, true, true, false)
        val entities = map(_dao.getPage(condition), History::class)
        val pageInfo = PageInfo(entities)
        return PageResult(pageInfo.total, pageInfo.list)
    }
}