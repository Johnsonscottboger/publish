package com.zentao.publish.service.svn.impl

import com.zentao.publish.dao.IProjectDao
import com.zentao.publish.dao.IUserDao
import com.zentao.publish.extensions.splitRemoveEmpty
import com.zentao.publish.service.svn.ISvnService
import com.zentao.publish.util.Encrypt
import com.zentao.publish.viewmodel.SvnCommitInput
import com.zentao.publish.viewmodel.SvnList
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import javax.annotation.Resource
import kotlin.io.path.Path

@Service
class DefaultSvnServiceImpl : ISvnService {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Resource
    private lateinit var _userDao: IUserDao

    @Resource
    private lateinit var _projectDao: IProjectDao

    @Value("\${publishpath}")
    private lateinit var _appdata: String

    override fun list(projectId: String): List<SvnList> {
        val project = _projectDao.getById(projectId) ?: return emptyList()
        val user = _userDao.getById(project.userId!!) ?: return emptyList()

        val output =
            exec(
                "svn list \"${project.publishPath!!}\" --verbose --username ${user.username} --password ${
                    Encrypt.decrypt(
                        user.password!!
                    )
                }"
            )

        return output.drop(1).filter { p -> p.endsWith("/") }.map { p ->
            val split = p.splitRemoveEmpty(" ")
            SvnList(
                split[0],
                split[1],
                split[5].removeSuffix("/")
            )
        }
    }

    private fun last(projectId: String): SvnList? {
        return list(projectId).lastOrNull()
    }

    override fun version(projectId: String): String {
        val project = _projectDao.getById(projectId) ?: throw NullPointerException()
        val lastVersion = last(projectId)
        var slotBlock = false
        var originIndex = -1
        val slotBuilder = StringBuilder()
        val versionBuilder = StringBuilder()

        try {
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
                        if (Pattern.matches("\\d*i", slot)) {
                            if (lastVersion == null)
                                versionBuilder.append("1".padStart(slot.length, '0'))
                            else {
                                //val lastIndex = lastVersion.entryName.substring(originIndex - slot.length, originIndex)
                                val lastIndexResult =
                                    Regex("\\d+").find(lastVersion.entryName.removePrefix(versionBuilder.toString()))
                                        ?: throw IllegalArgumentException("创建版本号失败, 找不到索引字段: $lastVersion")
                                val lastIndex = lastIndexResult.value
                                val i = lastIndex.toIntOrNull()
                                    ?: throw IllegalArgumentException("无法将索引字段转换为数字: $lastIndex")
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
            throw IllegalStateException(
                "创建下一版本时异常: 项目: ${project.name}, 版本号规则:${project.versionNameRule}, 最新版本号: $lastVersion",
                ex
            )
        }
        return versionBuilder.toString()
    }

    override fun create(projectId: String): String {
        val project = _projectDao.getById(projectId) ?: throw NullPointerException()
        val user = _userDao.getById(project.userId!!) ?: throw  NullPointerException()
        val version = version(projectId)
        val publishPath = "${project.publishPath!!}/${version}"
        val output =
            exec(
                "svn mkdir -m \"Making a dir\" \"${publishPath}\" --username ${user.username} --password ${
                    Encrypt.decrypt(
                        user.password!!
                    )
                }"
            )

        if (output.any { p -> p.startsWith("Committed") }) {
            //提交成功后检出到本地目录
            val path = Path(this._appdata, "publish", "project", project.name!!, version)
            val result =
                exec(
                    "svn checkout \"${publishPath}\" \"${path}\" --username ${user.username} --password ${
                        Encrypt.decrypt(
                            user.password!!
                        )
                    }"
                )
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
            last(input.projectId)?.entryName
        } ?: throw NullPointerException()

        val path = Path(this._appdata, "publish", "project", project.name!!, version)

        exec("svn add \"${path}\" --force")
        val output =
            exec("svn commit -m \"Commit\" \"${path}\" --username ${user.username} --password ${Encrypt.decrypt(user.password!!)}")
        return output.joinToString("\r\n")
    }

    override fun exec(args: String): List<String> {
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
}
