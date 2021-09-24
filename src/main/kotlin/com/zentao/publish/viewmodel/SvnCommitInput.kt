package com.zentao.publish.viewmodel

data class SvnCommitInput(
    val projectId: String,
    val version: String? = null
)
