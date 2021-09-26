package com.zentao.publish.entity

import java.util.*

data class PubSubscribe(
    var id: String? = null,
    var productId: String? = null,
    var projectId: String? = null,
    var productSubPath: String? = null,
    var lastProductVersion: String? = null,
    var lastProductTime: Date? = null,
    var createTime: Date? = null,
    var modifyTime: Date? = null
)
