package com.zentao.publish.eventbus

interface IEventHandler<in TEvent : IEvent> {
    fun handle(e: TEvent)
}