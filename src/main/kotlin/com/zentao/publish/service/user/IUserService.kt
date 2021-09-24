package com.zentao.publish.service.user

import com.zentao.publish.service.IMapService
import com.zentao.publish.viewmodel.User

interface IUserService : IMapService{

    fun create(user: User): String

    fun update(user: User)

    fun delete(id: String)

    fun getAll(): List<User>

    fun getById(id: String): User?

    fun getByName(name: String): User?
}