package com.zentao.publish.viewmodel

import java.util.*

data class Subscribe(
    val id: String? = null,
    val productId: String? = null,
    val projectId: String? = null,
    val productSubPath: String? = null,
    val lastProductVersion: String? = null,
    val lastProductTime: Date? = null,
    val createTime: Date? = null,
    val modifyTime: Date? = null,
)
