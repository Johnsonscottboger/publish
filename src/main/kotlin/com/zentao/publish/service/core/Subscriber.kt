package com.zentao.publish.service.core

import com.zentao.publish.dao.IUserDao
import com.zentao.publish.event.DelayUpdateEvent
import com.zentao.publish.event.SvnUpdateEvent
import com.zentao.publish.eventbus.IEventHandler
import com.zentao.publish.service.history.IHistoryService
import com.zentao.publish.service.mail.IMailService
import com.zentao.publish.service.subscribe.ISubscribeService
import com.zentao.publish.service.svn.ISvnService
import com.zentao.publish.util.Encrypt
import com.zentao.publish.viewmodel.History
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
import kotlin.io.path.*

@Component
class Subscriber : IEventHandler<SvnUpdateEvent> {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Resource
    private lateinit var userDao: IUserDao

    @Resource
    private lateinit var subscribeService: ISubscribeService

    @Resource
    private lateinit var historyService: IHistoryService

    @Resource
    private lateinit var svnService: ISvnService

    @Resource
    private lateinit var mailService: IMailService

    @Value("\${publishpath}")
    private lateinit var appdata: String

    override fun handle(e: SvnUpdateEvent) {
        val user = userDao.getById(e.project.userId!!)!!
        val publishPath = "${e.product.publishPath}/${e.subscribe.productSubPath}"

        val path =
            Path(
                this.appdata,
                "publish",
                "product",
                e.product.name!!,
                e.subscribe.productSubPath!!,
                e.lastVersion.entryName
            )
        if (!path.toFile().exists()) {
            if (path.parent.exists()) {
                svnService.exec(
                    "svn update \"${path.parent}\" --username ${user.username} --password ${
                        Encrypt.decrypt(
                            user.password!!
                        )
                    }"
                )
            } else {
                log.info("\t??????????????????, ??????????????????...")
                svnService.exec(
                    "svn checkout \"${publishPath}\" \"${path.parent}\" --username ${user.username} --password ${
                        Encrypt.decrypt(
                            user.password!!
                        )
                    }"
                )
            }
        }

        if(e.subscribe.delay == 1) {
            historyService.create(
                History(
                    id = UUID.randomUUID().toString(),
                    productId = e.product.id,
                    projectId = e.project.id,
                    productVersion = e.lastVersion.entryName,
                    projectVersion = null,
                    published = 0,
                    publishTime = Date(),
                    createTime = Date()
                )
            )
            return
        }

        val projectVersion = svnService.create(e.project.id!!)
        log.info("\t?????????????????????:${Path(projectVersion).name}")
        path.copyTo(Path(projectVersion, e.lastVersion.entryName), true)
        createDeployDoc(projectVersion, e.product.name, e.lastVersion.entryName)
        svnService.commit(SvnCommitInput(e.project.id!!, Path(projectVersion).name))
        log.info("\t?????????????????????:${Path(projectVersion).name}")
        val subscribe = e.subscribe.copy(lastProductVersion = e.lastVersion.entryName, lastProductTime = Date())
        subscribeService.update(subscribe)
        historyService.create(
            History(
                id = UUID.randomUUID().toString(),
                productId = e.product.id,
                projectId = e.project.id,
                productVersion = e.lastVersion.entryName,
                projectVersion = Path(projectVersion).name,
                published = 1,
                publishTime = Date(),
                createTime = Date()
            )
        )
        log.info("\t?????????????????????:${user.email}")
        mailService.send(
            user.email!!, MailSendInfo(
                productName = e.product.name,
                productPublishPath = "${publishPath}/${e.lastVersion.entryName}",
                publishDate = SimpleDateFormat("yyyy/MM/dd HH:mm").format(Date()),
                projectName = e.project.name!!,
                projectVersion = Path(projectVersion).name,
                projectPublishPath = "${e.project.publishPath!!.removeSuffix("/")}/${
                    Path(
                        projectVersion
                    ).name
                }",
                zentaoAddress = "http://zentao.wuhanins.com:88/zentao/my/",
                description = "${e.product.name}????????????: ${e.lastVersion.entryName}"
            )
        )
        log.info("\t???????????????")
    }

    private fun createDeployDoc(projectVersionPath: String, productName: String, productVersion: String) {
        val projectVersion = Path(projectVersionPath).name
        val templatePath = Path(projectVersionPath).parent.toString()
        val templateFile = if (File(templatePath, "?????????????????????.doc").exists()) {
            File(templatePath, "?????????????????????.doc")
        } else if (File(templatePath, "?????????????????????.docx").exists()) {
            File(templatePath, "?????????????????????.docx")
        } else {
            null
        }
        if (templateFile == null) return

        FileInputStream(templateFile).use { input ->
            val doc = HWPFDocument(input)
            val range = doc.range
            range.replaceText("\${????????????}", SimpleDateFormat("yyyy-MM-dd").format(Date()))
            range.replaceText("\${????????????}", projectVersion)
            range.replaceText("\${????????????}", "${productName}????????????: $productVersion")
            FileOutputStream(Path(projectVersionPath, templateFile.name).toFile()).use { output ->
                doc.write(output)
            }
        }
    }
}