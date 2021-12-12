package com.zentao.publish.entity

import java.util.*

data class PubHistory(
    var id: String? = null,
    var productId: String? = null,
    var projectId: String? = null,
    var productVersion: String? = null,
    var projectVersion: String? = null,
    val published: Int? = null,
    var publishTime: Date? = null,
    var createTime: Date? = null
)
