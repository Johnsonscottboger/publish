package com.zentao.publish.condition

import com.zentao.publish.viewmodel.PageParam

data class ProductPageCondition(
    val name: String? = null,
    override val page: PageParam
) : PageCondition(page)
