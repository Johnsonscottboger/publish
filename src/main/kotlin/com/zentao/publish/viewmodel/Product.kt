package com.zentao.publish.viewmodel

import java.util.*

data class Product(
    val id: String? = null,
    val name: String? = null,
    val publishPath: String? = null,
    val createTime: Date? = null,
    val modifyTime: Date? = null
)
