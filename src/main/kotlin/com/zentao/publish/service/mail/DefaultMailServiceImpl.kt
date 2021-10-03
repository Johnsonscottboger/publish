package com.zentao.publish.service.mail

import com.zentao.publish.viewmodel.MailSendInfo
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.*
import javax.annotation.Resource

@Service
class DefaultMailServiceImpl : IMailService {

    @Resource
    private lateinit var sender: JavaMailSender

    @Resource
    private lateinit var templateEngine: TemplateEngine

    override fun send(to: String, info: MailSendInfo) {
        val context = Context().apply {
            setVariable("productName", info.productName)
            setVariable("productPublishPath", info.productPublishPath)
            setVariable("projectName", info.projectName)
            setVariable("projectVersion", info.projectVersion)
            setVariable("projectPublishPath", info.projectPublishPath)
            setVariable("zentaoAddress", info.zentaoAddress)
        }
        val text = templateEngine.process("mail.html", context)
        val message = sender.createMimeMessage()
        MimeMessageHelper(message, true).apply {
            setSubject("【产品发布通知】")
            setFrom("wangnb@wuhanins.com")
            setTo(to)
            setSentDate(Date())
            setText(text, true)
        }
        sender.send(message)
    }

    override fun errorReport(text: String, exception: Throwable) {
        val message = SimpleMailMessage().apply {
            setSubject("【产品发布通知错误报告】")
            setFrom("wangnb@wuhanins.com")
            setTo("wangnb@wuhanins.com")
            setSentDate(Date())
            setText(exception.toString())
        }
        sender.send(message)
    }
}