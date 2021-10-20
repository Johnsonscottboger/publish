package com.zentao.publish.viewmodel

import io.swagger.annotations.Api
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.util.*

@ApiModel
data class User(
    @ApiModelProperty("主键")
    val id: String? = null,

    @ApiModelProperty("姓名", required = true)
    val name: String? = null,

    @ApiModelProperty("邮箱地址", required = true, example = "版本提交后会发送反馈邮件, 建议安装邮箱客户端并接收推送.")
    val email: String? = null,

    @ApiModelProperty("SVN账号, 用于在项目中创建版本", required = true)
    val username: String? = null,

    @ApiModelProperty("SVN密码, 用于在项目中创建版本, 会加密保存", required = true)
    val password: String? = null,

    @ApiModelProperty("创建时间", hidden = true)
    val createTime: Date? = null,

    @ApiModelProperty("修改时间", hidden = true)
    val modifyTime: Date? = null
)