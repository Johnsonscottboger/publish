package com.zentao.publish.service.user.impl

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.zentao.publish.condition.UserPageCondition
import com.zentao.publish.dao.IUserDao
import com.zentao.publish.entity.PubUser
import com.zentao.publish.service.user.IUserService
import com.zentao.publish.util.Encrypt
import com.zentao.publish.viewmodel.PageResult
import com.zentao.publish.viewmodel.User
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.Resource

@Service
class DefaultUserServiceImpl : IUserService {

    @Resource
    private lateinit var _dao: IUserDao

    override fun create(user: User): String {
        val entity = map(user, PubUser::class) ?: throw TypeCastException()
        entity.id = UUID.randomUUID().toString()
        entity.createTime = Date()
        entity.password = Encrypt.encrypt(entity.password!!)
        _dao.create(entity)
        return entity.id!!
    }

    override fun update(user: User) {
        val entity = map(user, PubUser::class) ?: throw TypeCastException()
        entity.modifyTime = Date()
        entity.password = Encrypt.encrypt(entity.password!!)
        _dao.update(entity)
    }

    override fun delete(id: String) {
        _dao.delete(id)
    }

    override fun getAll(): List<User> {
        val entities = _dao.getAll()
        return map(entities, User::class)
    }

    override fun getById(id: String): User? {
        val entity = _dao.getById(id) ?: return null
        return map(entity, User::class) ?: throw TypeCastException()
    }

    override fun getByName(name: String): User? {
        val entity = _dao.getByName(name) ?: return null
        return map(entity, User::class) ?: throw TypeCastException()
    }

    override fun getPage(condition: UserPageCondition): PageResult<User> {
        PageHelper.startPage<User>(condition.page.pageIndex + 1, condition.page.pageSize, true, true, false)
        val entities = map(_dao.getPage(condition), User::class)
        val pageInfo = PageInfo(entities)
        return PageResult(pageInfo.total, pageInfo.list.map { p -> p.copy(password = "******") })
    }
}