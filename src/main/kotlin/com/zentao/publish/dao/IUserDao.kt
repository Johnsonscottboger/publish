package com.zentao.publish.dao

import com.zentao.publish.entity.PubUser

interface IUserDao {

    fun create(entity: PubUser)

    fun update(entity: PubUser)

    fun delete(id: String)

    fun getAll(): List<PubUser>

    fun getById(id: String): PubUser?

    fun getByName(name: String): PubUser?
}