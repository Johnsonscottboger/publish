package com.zentao.publish.condition

import com.zentao.publish.viewmodel.PageParam

data class SubscribePageCondition(
    val productId: String? = null,
    val projectId: String? = null,
    val lastProductVersion: String? = null,
    override val page: PageParam
) : PageCondition(page)
