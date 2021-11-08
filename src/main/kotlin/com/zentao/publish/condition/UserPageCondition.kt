package com.zentao.publish.condition

import com.zentao.publish.viewmodel.PageParam

data class UserPageCondition(
    val name: String? = null,
    val email: String? = null,
    val username: String? = null,
    override val page: PageParam
) : PageCondition(page)
