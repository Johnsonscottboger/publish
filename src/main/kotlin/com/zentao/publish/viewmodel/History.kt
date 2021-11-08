package com.zentao.publish.viewmodel

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.util.*

@ApiModel("历史记录")
data class History(
    @ApiModelProperty("主键")
    val id: String? = null,

    @ApiModelProperty("产品ID")
    val productId: String? = null,

    @ApiModelProperty("项目ID")
    val projectId: String? = null,

    @ApiModelProperty("产品版本号")
    val productVersion: String? = null,

    @ApiModelProperty("项目版本号")
    val projectVersion: String? = null,

    @ApiModelProperty("发布时间")
    val publishTime: Date? = null,

    @ApiModelProperty("创建时间")
    val createTime: Date? = null
)
