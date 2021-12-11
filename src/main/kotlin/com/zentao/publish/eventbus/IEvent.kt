package com.zentao.publish.eventbus

import java.time.LocalDateTime

interface IEvent {
    val raiseTime: LocalDateTime
        get() = LocalDateTime.now()
}