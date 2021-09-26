package com.zentao.publish.entity

import java.util.*

data class PubProject(
        var id: String? = null,
        var userId: String? = null,
        var name: String? = null,
        var publishPath: String? = null,
        var versionNameRule: String? = null,
        var createTime: Date? = null,
        var modifyTime: Date? = null,
)