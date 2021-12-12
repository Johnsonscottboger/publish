package com.zentao.publish.eventbus.impl

import com.zentao.publish.eventbus.IEvent
import com.zentao.publish.eventbus.IEventBus
import com.zentao.publish.eventbus.IEventHandler
import org.springframework.beans.BeanUtils
import org.springframework.context.support.ApplicationObjectSupport
import org.springframework.stereotype.Service
import java.lang.reflect.ParameterizedType
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class EventBus : IEventBus, ApplicationObjectSupport() {

    override fun <TEvent : IEvent, TEventHandler : IEventHandler<TEvent>> subscribe(eventHandlerType: Class<TEventHandler>) {
        synchronized(this) {
            val eventType =
                (eventHandlerType.genericInterfaces[0] as ParameterizedType).actualTypeArguments[0] as Class<IEvent>
            val list = eventHandlerMap.getOrPut(eventType) { mutableListOf() } as MutableList
            val type = eventHandlerType as Class<IEventHandler<IEvent>>
            if (!list.contains(type))
                list.add(type)
        }
    }

    override fun <TEvent : IEvent, TEventHandler : IEventHandler<TEvent>> unsubscribe(eventHandlerType: Class<TEventHandler>) {
        synchronized(this) {
            val eventType =
                (eventHandlerType.genericInterfaces[0] as ParameterizedType).actualTypeArguments[0] as Class<IEvent>
            if (eventHandlerMap.containsKey(eventType)) {
                val list = eventHandlerMap[eventType] as MutableList
                list.remove(eventHandlerType as Class<IEventHandler<IEvent>>)
            }
        }
    }

    override fun <TEvent : IEvent> publish(event: TEvent) {
        synchronized(this) {
            val eventType = event::class.java as Class<IEvent>
            if (!eventHandlerMap.containsKey(eventType)) {
                applicationContext?.getBeansOfType(IEventHandler::class.java)
                    ?.forEach { p -> subscribe(p.value::class.java) }
            }

            val list = eventHandlerMap[eventType] as MutableList?
            list?.forEach { item ->
                val provider = this.applicationContext?.getBeanProvider(item)
                val instance = provider?.getObject() ?: BeanUtils.instantiateClass(item)
                instance.handle(event)
            }
        }
    }

    companion object {
        private val eventHandlerMap = ConcurrentHashMap<Class<IEvent>, List<Class<IEventHandler<IEvent>>>>()
    }
}
