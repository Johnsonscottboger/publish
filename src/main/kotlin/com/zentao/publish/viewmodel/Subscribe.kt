package com.zentao.publish.viewmodel

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.util.*

@ApiModel("订阅")
data class Subscribe(
    @ApiModelProperty("主键")
    val id: String? = null,

    @ApiModelProperty("产品ID", required = true)
    val productId: String? = null,

    @ApiModelProperty("项目ID", required = true)
    val projectId: String? = null,

    @ApiModelProperty("产品发布子路径, 部分项目需要订阅产品的特定版本, 例如托巴项目使用劳务产品的业主版本", required = false, example = "/业主版本")
    val productSubPath: String? = null,

    @ApiModelProperty("项目已更新到的最新产品版本文件名", required = false, example = "basedatav1.1.029.211009.rar")
    val lastProductVersion: String? = null,

    @ApiModelProperty("最新版本更新时间", hidden = true)
    val lastProductTime: Date? = null,

    @ApiModelProperty("创建时间", hidden = true)
    val createTime: Date? = null,

    @ApiModelProperty("修改时间", hidden = true)
    val modifyTime: Date? = null,
)
