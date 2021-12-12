package com.zentao.publish.event

import com.zentao.publish.eventbus.IEvent
import com.zentao.publish.viewmodel.History
import com.zentao.publish.viewmodel.Product
import com.zentao.publish.viewmodel.Project

class DelayUpdateEvent(
    val project: Project,
    val histories: List<History>
) : IEvent