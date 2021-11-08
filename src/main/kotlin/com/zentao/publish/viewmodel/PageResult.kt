package com.zentao.publish.viewmodel

data class PageResult<T>(
    val totalRows: Long,
    val data: List<T>
)
