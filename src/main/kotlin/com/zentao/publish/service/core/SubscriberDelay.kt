package com.zentao.publish.service.core

import com.zentao.publish.dao.IProductDao
import com.zentao.publish.dao.ISubscribeDao
import com.zentao.publish.dao.IUserDao
import com.zentao.publish.event.DelayUpdateEvent
import com.zentao.publish.eventbus.IEventHandler
import com.zentao.publish.service.history.IHistoryService
import com.zentao.publish.service.mail.IMailService
import com.zentao.publish.service.svn.ISvnService
import com.zentao.publish.util.Encrypt
import com.zentao.publish.viewmodel.MailSendInfo
import com.zentao.publish.viewmodel.SvnCommitInput
import org.apache.poi.hwpf.HWPFDocument
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.Resource
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.exists
import kotlin.io.path.name

@Component
class SubscriberDelay : IEventHandler<DelayUpdateEvent> {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Resource
    private lateinit var userDao: IUserDao

    @Resource
    private lateinit var productDao: IProductDao

    @Resource
    private lateinit var subscribeDao: ISubscribeDao

    @Resource
    private lateinit var historyService: IHistoryService

    @Resource
    private lateinit var svnService: ISvnService

    @Resource
    private lateinit var mailService: IMailService

    @Value("\${publishpath}")
    private lateinit var appdata: String

    override fun handle(e: DelayUpdateEvent) {
        val productList = productDao.getAll()
        val subscribeList = subscribeDao.getAll()

        val user = userDao.getById(e.project.userId!!)!!
        val projectVersionPath = svnService.create(e.project.id!!)
        log.info("\t项目版本已创建:${Path(projectVersionPath).name}")

        for (history in e.histories) {
            val product = productList.find { p -> p.id == history.productId } ?: continue
            val subscribe =
                subscribeList.find { p -> p.projectId == e.project.id && p.productId == history.productId } ?: continue

            val sourceFile = Path(
                appdata,
                "publish",
                "product",
                product.name!!,
                subscribe.productSubPath!!,
                history.productVersion!!
            )

            if (!sourceFile.toFile().exists()) {
                if (sourceFile.parent.exists()) {
                    svnService.exec(
                        "svn update \"${sourceFile.parent}\" --username ${user.username} --password ${
                            Encrypt.decrypt(
                                user.password!!
                            )
                        }"
                    )
                } else {
                    val productPath = "${product.publishPath}/${subscribe.productSubPath}"
                    svnService.exec(
                        "svn checkout \"${productPath}\" \"${sourceFile.parent}\" --username ${user.username} --password ${
                            Encrypt.decrypt(
                                user.password!!
                            )
                        }"
                    )
                }
            }
            sourceFile.copyTo(Path(projectVersionPath, history.productVersion), true)

            subscribe.lastProductTime = Date()
            subscribe.lastProductVersion = history.productVersion
            subscribeDao.update(subscribe)
        }

        createDeployDoc(projectVersionPath, *e.histories.map { p -> p.productVersion!! }.toTypedArray())
        svnService.commit(SvnCommitInput(e.project.id, Path(projectVersionPath).name))
        log.info("\t项目版本已提交:${Path(projectVersionPath).name}")
        e.histories.forEach { history ->
            val entity =
                history.copy(published = 1, projectVersion = Path(projectVersionPath).name, publishTime = Date())
            historyService.update(entity)
        }
        log.info("\t准备发送邮件至:${user.email}")
        mailService.send(
            user.email!!, MailSendInfo(
                productName = "产品集合",
                productPublishPath = "产品集合",
                publishDate = SimpleDateFormat("yyyy/MM/dd HH:mm").format(Date()),
                projectName = e.project.name!!,
                projectVersion = Path(projectVersionPath).name,
                projectPublishPath = "${e.project.publishPath!!.removeSuffix("/")}/${
                    Path(
                        projectVersionPath
                    ).name
                }",
                zentaoAddress = "http://zentao.wuhanins.com:88/zentao/my/",
                description = "产品集合更新: ${e.histories.map { p -> p.productVersion }.joinToString(", ")}"
            )
        )
        log.info("\t邮件已发送")
    }

    private fun createDeployDoc(projectVersionPath: String, vararg productVersion: String) {
        val projectVersion = Path(projectVersionPath).name
        val templatePath = Path(projectVersionPath).parent.toString()
        val templateFile = if (File(templatePath, "上线部署控制表.doc").exists()) {
            File(templatePath, "上线部署控制表.doc")
        } else if (File(templatePath, "上线部署控制表.docx").exists()) {
            File(templatePath, "上线部署控制表.docx")
        } else {
            null
        }
        if (templateFile == null) return

        FileInputStream(templateFile).use { input ->
            val doc = HWPFDocument(input)
            val range = doc.range
            range.replaceText("\${提测日期}", SimpleDateFormat("yyyy-MM-dd").format(Date()))
            range.replaceText("\${提测版本}", projectVersion)
            range.replaceText("\${部署说明}", "产品更新: ${productVersion.joinToString()}")
            FileOutputStream(Path(projectVersionPath, templateFile.name).toFile()).use { output ->
                doc.write(output)
            }
        }
    }
}