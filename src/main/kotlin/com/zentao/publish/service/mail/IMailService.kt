package com.zentao.publish.service.mail

import com.zentao.publish.viewmodel.MailSendInfo

interface IMailService {

    fun send(to: String, info: MailSendInfo)

    fun errorReport(text: String, exception: Throwable)
}