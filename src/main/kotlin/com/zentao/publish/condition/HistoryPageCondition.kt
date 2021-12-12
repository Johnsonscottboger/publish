package com.zentao.publish.condition

import com.zentao.publish.viewmodel.PageParam
import java.util.*

data class HistoryPageCondition(
    val productId: String? = null,
    val projectId: String? = null,
    val productVersion: String? = null,
    val projectVersion: String? = null,
    val published: Int? = null,
    val publishStartTime: Date? = null,
    val publishEndTime: Date? = null,
    override val page: PageParam = PageParam(0)
) : PageCondition(page)
