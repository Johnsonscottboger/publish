package com.zentao.publish.viewmodel

import java.util.*

data class Project(
    val id: String? = null,
    val userId: String? = null,
    val project: String? = null,
    val publishPath: String? = null,
    val versionNameRule: String? = null,
    val createTime: Date? = null,
    val modifyTime: Date? = null
)
