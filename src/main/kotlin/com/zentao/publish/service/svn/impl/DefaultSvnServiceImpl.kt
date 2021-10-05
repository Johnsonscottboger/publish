package com.zentao.publish.service.svn.impl

import com.zentao.publish.dao.IProductDao
import com.zentao.publish.dao.IProjectDao
import com.zentao.publish.dao.ISubscribeDao
import com.zentao.publish.dao.IUserDao
import com.zentao.publish.extensions.splitRemoveEmpty
import com.zentao.publish.service.mail.IMailService
import com.zentao.publish.service.svn.ISvnService
import com.zentao.publish.viewmodel.MailSendInfo
import com.zentao.publish.viewmodel.SvnCommitInput
import com.zentao.publish.viewmodel.SvnList
import org.apache.poi.hwpf.HWPFDocument
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import javax.annotation.Resource
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.exists
import kotlin.io.path.name

@Service
class DefaultSvnServiceImpl : ISvnService {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Resource
    private lateinit var _userDao: IUserDao

    @Resource
    private lateinit var _productDao: IProductDao

    @Resource
    private lateinit var _projectDao: IProjectDao

    @Resource
    private lateinit var _subscribeDao: ISubscribeDao

    @Autowired
    private lateinit var _mailService: IMailService

    override fun list(projectId: String): List<SvnList> {
        val project = _projectDao.getById(projectId) ?: return emptyList()
        val user = _userDao.getById(project.userId!!) ?: return emptyList()

        val output =
            exec("svn list \"${project.publishPath!!}\" --verbose --username ${user.username} --password ${user.password}")

        return output.drop(1).map { p ->
            val split = p.splitRemoveEmpty(" ")
            SvnList(
                split[0],
                split[1],
                split[5].removeSuffix("/")
            )
        }
    }

    override fun version(projectId: String): String {
        val project = _projectDao.getById(projectId) ?: throw NullPointerException()
        val lastVersion = list(projectId).maxByOrNull { p -> p.revision.toInt() }
        var slotBlock = false
        var originIndex = -1
        val slotBuilder = StringBuilder()
        val versionBuilder = StringBuilder()

        project.versionNameRule!!.forEach { char ->
            originIndex++
            when (char) {
                '{' -> {
                    slotBlock = true
                    originIndex--
                }
                '}' -> {
                    slotBlock = false
                    val slot = slotBuilder.toString()
                    if (Pattern.matches("\\d+i", slot)) {
                        if (lastVersion == null)
                            versionBuilder.append("1".padStart(slot.length, '0'))
                        else {
                            val lastIndex = lastVersion.entryName.substring(originIndex - slot.length, originIndex)
                            versionBuilder.append((lastIndex.toInt() + 1).toString().padStart(slot.length, '0'))
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
        return versionBuilder.toString()
    }

    override fun create(projectId: String): String {
        val project = _projectDao.getById(projectId) ?: throw NullPointerException()
        val user = _userDao.getById(project.userId!!) ?: throw  NullPointerException()
        val version = version(projectId)
        val publishPath = "${project.publishPath!!}/${version}"
        val output =
            exec("svn mkdir -m \"Making a dir\" \"${publishPath}\" --username ${user.username} --password ${user.password}")

        if (output.any { p -> p.startsWith("Committed") }) {
            //提交成功后检出到本地目录
            val path = Path(System.getenv("appdata"), "publish", "project", project.name!!, version)
            val result =
                exec("svn checkout \"${publishPath}\" \"${path}\" --username ${user.username} --password ${user.password}")
            if (result.any { p -> p.startsWith("Checked") }) {
                return path.toString()
            }
        }
        return output.joinToString()
    }

    override fun commit(input: SvnCommitInput): String {
        val project = _projectDao.getById(input.projectId) ?: throw  NullPointerException()
        val user = _userDao.getById(project.userId!!) ?: throw  NullPointerException()
        val version = if (!input.version.isNullOrEmpty()) input.version else {
            list(input.projectId).maxByOrNull { p -> p.revision.toInt() }?.entryName
        } ?: throw NullPointerException()

        val path = Path(System.getenv("appdata"), "publish", "project", project.name!!, version)

        exec("svn add \"${path}\" --force")
        val output =
            exec("svn commit -m \"Commit\" \"${path}\" --username ${user.username} --password ${user.password}")
        return output.joinToString("\r\n")
    }

    @Scheduled(fixedRate = 600000, initialDelay = 10000)
    override fun listenProduct() {
        log.info("开始检查更新")
        try {
            val userList = _userDao.getAll()
            val productList = _productDao.getAll()
            val projectList = _projectDao.getAll()
            val subscribeList = _subscribeDao.getAll()

            log.info("共查询到user-${userList.count()}个, product-${productList.count()}个, project-${projectList.count()}个, subscribe-${subscribeList.count()}个")

            for (product in productList) {
                log.info("当前产品:${product.id} ${product.name}")
                val subscribes = subscribeList.filter { p -> p.productId == product.id }
                log.info("\t订阅数量:${subscribes.count()}")
                for (subscribe in subscribes) {
                    val project = projectList.find { p -> p.id == subscribe.projectId } ?: continue
                    val user = userList.find { p -> p.id == project.userId } ?: continue
                    val publishPath = "${product.publishPath}/${subscribe.productSubPath}"
                    log.info("\t订阅项目:${project.id} ${project.name}")
                    val list =
                        exec("svn list \"${publishPath}\" --verbose --username ${user.username} --password ${user.password}").drop(
                            1
                        ).map { p ->
                            val split = p.splitRemoveEmpty(" ")
                            SvnList(split[0], split[1], split[6].removeSuffix("/"))
                        }

                    val lastVersion = list.maxByOrNull { p -> p.revision.toInt() } ?: continue
                    log.info("\t产品最新版本:${lastVersion.entryName}")

                    val currentVersion = subscribe.lastProductVersion
                    log.info("\t项目最新版本:${currentVersion}")
                    if (currentVersion == null || lastVersion.entryName != currentVersion) {
                        log.info("当前项目需要更新")
                        val path =
                            Path(System.getenv("appdata"), "publish", "product", product.name!!, lastVersion.entryName)
                        if (!path.toFile().exists()) {
                            if (path.parent.exists()) {
                                exec("svn update \"${path.parent}\" --username ${user.username} --password ${user.password}")
                            } else {
                                exec("svn checkout \"${publishPath}\" \"${path.parent}\" --username ${user.username} --password ${user.password}")
                            }
                        }

                        val projectVersion = create(project.id!!)
                        log.info("\t项目版本已创建:${Path(projectVersion).name}")
                        path.copyTo(Path(projectVersion, lastVersion.entryName), true)
                        createDeployDoc(projectVersion, product.name!!, lastVersion.entryName)
                        commit(SvnCommitInput(project.id!!, Path(projectVersion).name))
                        log.info("\t项目版本已提交:${Path(projectVersion).name}")
                        subscribe.lastProductVersion = lastVersion.entryName
                        subscribe.lastProductTime = Date()
                        _subscribeDao.update(subscribe)
                        log.info("\t准备发送邮件至:${user.email}")
                        _mailService.send(
                            user.email!!, MailSendInfo(
                                productName = product.name!!,
                                productPublishPath = "${publishPath}/${lastVersion.entryName}",
                                projectName = project.name!!,
                                projectVersion = Path(projectVersion).name,
                                projectPublishPath = "${project.publishPath!!.removeSuffix("/")}/${Path(projectVersion).name}",
                                zentaoAddress = "http://zentao.wuhanins.com:88/zentao/my/",
                                description = ""
                            )
                        )
                        log.info("\t邮件已发送")
                    } else {
                        log.info("\t当前项目不需要更新")
                    }
                }
            }
        } catch (error: Throwable) {
            log.error("检查更新异常", error)
            _mailService.errorReport(error.message!!, error)
            log.info("检查更新异常报告已发送")
        }
    }

    private fun exec(args: String): List<String> {
        val process = Runtime.getRuntime().exec(args)
        val lines = process.inputStream.reader(Charset.forName("GBK")).use {
            it.readLines()
        }
        val error = process.errorStream.reader(Charset.forName("GBK")).use {
            it.readLines()
        }
        val exitValue = process.waitFor()
        if (exitValue != 0)
            throw RuntimeException(StringBuilder().apply { error.forEach(this::appendLine) }.toString())
        return lines
    }

    private fun createDeployDoc(projectVersionPath: String, productName: String, productVersion: String) {
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
            range.replaceText("\${部署说明}", "${productName}产品更新: $productVersion")
            FileOutputStream(Path(projectVersionPath, templateFile.name).toFile()).use { output ->
                doc.write(output)
            }
        }
    }
}