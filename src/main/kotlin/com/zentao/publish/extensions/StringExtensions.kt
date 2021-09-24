package com.zentao.publish.extensions

fun String.splitRemoveEmpty(vararg delimiters: String, ignoreCase: Boolean = false, limit: Int = 0) : List<String> {
    val result = this.split(delimiters = delimiters, ignoreCase, limit)
    return result.filter { p -> p.isNotBlank() }
}