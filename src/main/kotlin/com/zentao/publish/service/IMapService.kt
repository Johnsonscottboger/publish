package com.zentao.publish.service

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

interface IMapService {
    fun <N: Any> map(old: Any?, newClass: KClass<N>) : N? {
        if(old == null) return null
        val oldClass = old::class
        val ctor = newClass.primaryConstructor ?: return null
        val parameters = ctor.parameters.map { p ->
            val oldProp = oldClass.memberProperties.firstOrNull { o -> o.name == p.name } ?: return@map null
            oldProp.call(old)
        }
        val new = ctor.call(*parameters.toTypedArray())
        oldClass.memberProperties.forEach { p ->
            p.isAccessible = true
            val value = p.call(old)

            val destProp = newClass.memberProperties.firstOrNull { d -> d.name == p.name }
            if(destProp != null && destProp is KMutableProperty<*>) {
                destProp.isAccessible = true
                destProp.setter.call(new, value)
            }
        }
        return new
    }

    fun <O: Any, N : Any> map(oldList: List<O>, newClass: KClass<N>) : List<N> {
        if(oldList.isEmpty()) return emptyList()

        return oldList.map { old ->
            map(old, newClass)!!
        }
    }
}