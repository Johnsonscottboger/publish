package com.zentao.publish

import com.zentao.publish.dao.IHistoryDao
import com.zentao.publish.entity.PubHistory
import com.zentao.publish.event.SvnUpdateEvent
import com.zentao.publish.eventbus.IEventBus
import com.zentao.publish.eventbus.IEventHandler
import com.zentao.publish.extensions.deleteRec
import com.zentao.publish.service.mail.IMailService
import com.zentao.publish.viewmodel.MailSendInfo
import com.zentao.publish.viewmodel.Project
import com.zentao.publish.viewmodel.SvnList
import org.apache.poi.hwpf.HWPFDocument
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.Key
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import javax.annotation.Resource
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import kotlin.io.path.*

@SpringBootTest
class PublishApplicationTests {

    @Value("\${publishpath}")
    private lateinit var _appdata: String

    @Autowired
    private lateinit var _mailService: IMailService

    //@Test
    fun send() {
        val info = MailSendInfo(
            productName = "2021D01 英思基础数据中台",
            productPublishPath = "http://psvn.wuhanins.com/svn/产品发布库/2021D01 基础数据中台/basedata11.030.rar",
            publishDate = SimpleDateFormat("yyyy/MM/dd").format(Date()),
            projectName = "TB水电站绿色智能建造",
            projectVersion = "1.01.{00i}.{yyMMdd}",
            projectPublishPath = "http://psvn.wuhanins.com/svn/2020/2020Z11 TB水电站绿色智能建造平台、通用管理系统建设及维护项目/工作库/2产品生产/2.4软件测试/2.4.4提测版本/",
            zentaoAddress = "http://zentao.wuhanins.com:88/zentao/my/",
            description = "基础数据中台产品更新: basedata11.030.rar"
        )
        _mailService.send("wangnb@wuhanins.com", info)
    }

    fun deployDoc() {
        val template = "C:\\Users\\Aning\\AppData\\Roaming\\publish\\project\\TB水电站绿色智能建造\\上线部署控制表.doc"
        FileInputStream(template).use { input ->
            val doc = HWPFDocument(input)
            val range = doc.range
            range.replaceText("\${提测日期}", "替换后的提测日期")
            range.replaceText("\${提测版本}", "替换后的提测版本")
            range.replaceText("\${部署说明}", "替换后的部署说明")

            FileOutputStream("C:\\Users\\Aning\\AppData\\Roaming\\publish\\project\\TB水电站绿色智能建造\\上线部署控制表1.doc").use { output ->
                doc.write(output)
            }
        }
    }

    //@Test
    fun encrypt() {
        val content = "123456"

        val encrypt = Base64.getEncoder().encodeToString(content.toByteArray())

        val decrypt = String(Base64.getDecoder().decode(encrypt))

        assert(content == decrypt)
    }

    //@Test
    fun version() {

        val lastVersion = SvnList(revision = "5918", userName = "yunfei", entryName = "1.01.037.211028")
        var slotBlock = false
        var originIndex = -1
        val slotBuilder = StringBuilder()
        val versionBuilder = StringBuilder()

        try {
            "1.01.{00i}.{yyMMdd}"!!.forEach { char ->
                originIndex++
                when (char) {
                    '{' -> {
                        slotBlock = true
                        originIndex--
                    }
                    '}' -> {
                        slotBlock = false
                        val slot = slotBuilder.toString()
                        if (Pattern.matches("\\d*i", slot)) {
                            if (lastVersion == null)
                                versionBuilder.append("1".padStart(slot.length, '0'))
                            else {
                                //val lastIndex = lastVersion.entryName.substring(originIndex - slot.length, originIndex)
                                val lastIndexResult = Regex("\\d+").find(lastVersion.entryName.removePrefix(versionBuilder.toString()))
                                    ?: throw IllegalArgumentException("创建版本号失败, 找不到索引字段")
                                val lastIndex = lastIndexResult.value
                                val i = lastIndex.toIntOrNull() ?: throw IllegalArgumentException("无法将索引字段转换为数字: ${lastIndex}")
                                versionBuilder.append((i + 1).toString().padStart(slot.length, '0'))
                            }
                        } else {
                            val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(slot))
                            versionBuilder.append(dateTime)
                        }
                        slotBuilder.clear()
                        originIndex--
                    }
                    else -> {
                        if (slotBlock) slotBuilder.append(char) else versionBuilder.append(char)
                    }
                }
            }
        } catch (ex: Exception) {
            throw IllegalStateException("", ex)
        }

        println(versionBuilder.toString())

    }

    //@Test
    fun projectUpdate() {

        val project = Project(name = "TB水电站")
        val oldProject = Project(name = "2020Z11 TB水电站绿色智能建造平台")

        if (oldProject != null && oldProject.name != project.name) {
            val oldProjectPath = Path(_appdata, "publish", "project", oldProject.name!!)
            if (oldProjectPath.exists()) {
                val templateFile = if (File(oldProjectPath.toString(), "上线部署控制表.doc").exists()) {
                    File(oldProjectPath.toString(), "上线部署控制表.doc")
                } else if (File(oldProjectPath.toString(), "上线部署控制表.docx").exists()) {
                    File(oldProjectPath.toString(), "上线部署控制表.docx")
                } else {
                    null
                }

                if (templateFile != null) {
                    val newProjectPath = Path(_appdata, "publish", "project", project.name!!)
                    if (!newProjectPath.exists()) newProjectPath.createDirectories()
                    templateFile.copyTo(File(newProjectPath.toString(), templateFile.name), true)
                }
            }
            oldProjectPath.deleteRec()
        }
    }

    @Resource
    private lateinit var _historyDao: IHistoryDao

    //@Test
    fun insertHistory() {
        _historyDao.create(
            PubHistory(
                id = UUID.randomUUID().toString(),
                productId = UUID.randomUUID().toString(),
                projectId = UUID.randomUUID().toString(),
                productVersion = "productVersion",
                projectVersion = "projectVersion",
                publishTime = Date(),
                createTime = Date()
            )
        )
    }

    @Resource
    private lateinit var eventBus: IEventBus

    class SvnUpdateHandler : IEventHandler<SvnUpdateEvent> {
        override fun handle(e: SvnUpdateEvent) {
            TODO("Not yet implemented")
        }
    }

    //@Test
    fun eventBusTest() {
        eventBus.subscribe(SvnUpdateHandler::class.java)

        eventBus.unsubscribe(SvnUpdateHandler::class.java)

        eventBus.subscribe(SvnUpdateHandler::class.java)
    }
}
