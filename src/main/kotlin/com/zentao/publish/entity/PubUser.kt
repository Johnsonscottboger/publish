package com.zentao.publish.entity

import java.util.*

data class PubUser(
        var id: String? = null,
        var username: String? = null,
        var password: String? = null,
        var createTime: Date? = null,
        var modifyTime: Date? = null
)