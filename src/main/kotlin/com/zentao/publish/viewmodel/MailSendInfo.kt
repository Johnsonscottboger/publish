package com.zentao.publish.viewmodel

data class MailSendInfo(
    val productName: String,
    val productPublishPath: String,
    val projectName: String,
    val projectVersion: String,
    val projectPublishPath: String,
    val zentaoAddress: String,
    val description: String,
)
