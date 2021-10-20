package com.zentao.publish.viewmodel

import io.swagger.annotations.Api
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.util.*

@ApiModel("项目")
data class Project(
    @ApiModelProperty("主键")
    val id: String? = null,

    @ApiModelProperty("项目负责人ID", required = true)
    val userId: String? = null,

    @ApiModelProperty("项目名称", required = true, example = "TB水电站绿色智能建造")
    val name: String? = null,

    @ApiModelProperty("项目提测发布版本路径", required = true)
    val publishPath: String? = null,

    @ApiModelProperty("项目版本命名规则, {001}:表示3位流水号, {yyMMdd}表示日期", required = true, example = "1.01.{00i}.{yyMMdd}")
    val versionNameRule: String? = null,

    @ApiModelProperty("创建时间", hidden = true)
    val createTime: Date? = null,

    @ApiModelProperty("修改时间", hidden = true)
    val modifyTime: Date? = null
)
