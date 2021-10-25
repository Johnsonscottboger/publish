package com.zentao.publish.viewmodel

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.annotations.Api
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.apache.ibatis.type.JdbcType
import java.util.*

@ApiModel("产品")
data class Product(
    @ApiModelProperty("主键")
    val id: String? = null,

    @ApiModelProperty("产品名称", required = true, example = "基础数据中台")
    val name: String? = null,

    @ApiModelProperty("产品发布路径", required = true, example = "http://psvn.wuhanins.com/svn/产品发布库/2021D01 基础数据中台/")
    val publishPath: String? = null,

    @ApiModelProperty("创建时间", hidden = true)
    val createTime: Date? = null,

    @ApiModelProperty("修改时间", hidden = true)
    val modifyTime: Date? = null,
)
