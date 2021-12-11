package com.zentao.publish.event

import com.zentao.publish.eventbus.IEvent
import com.zentao.publish.viewmodel.Product
import com.zentao.publish.viewmodel.Project
import com.zentao.publish.viewmodel.Subscribe
import com.zentao.publish.viewmodel.SvnList

class SvnUpdateEvent(
    val product: Product,
    val project: Project,
    val subscribe: Subscribe,
    val lastVersion: SvnList
) : IEvent