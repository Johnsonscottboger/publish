package com.zentao.publish.util

import java.util.*

class Encrypt {

    companion object {
        fun encrypt(content: String): String {
            return Base64.getEncoder().encodeToString(content.toByteArray())
        }

        fun decrypt(content: String) : String {
            return String(Base64.getDecoder().decode(content))
        }
    }
}