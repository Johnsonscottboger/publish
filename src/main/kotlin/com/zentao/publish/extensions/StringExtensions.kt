package com.zentao.publish.extensions

fun String.splitRemoveEmpty(vararg delimiters: String) : List<String> {
    val result = this.split(*delimiters)
    return result.filter { p -> p.isNotBlank() }
}