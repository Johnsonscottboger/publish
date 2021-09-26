package com.zentao.publish.entity

import java.util.*

data class PubProduct(
    var id: String? = null,
    var name: String? = null,
    var publishPath: String? = null,
    var createTime: Date? = null,
    var modifyTime: Date? = null
)
