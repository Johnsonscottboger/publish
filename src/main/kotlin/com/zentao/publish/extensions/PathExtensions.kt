package com.zentao.publish.extensions

import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries


fun Path.deleteRec() {
    if (this.isDirectory()) {
        val entries = this.listDirectoryEntries()
        if (entries.any()) {
            entries.forEach { p -> p.deleteRec() }
        }
    } else
        this.deleteIfExists()
}