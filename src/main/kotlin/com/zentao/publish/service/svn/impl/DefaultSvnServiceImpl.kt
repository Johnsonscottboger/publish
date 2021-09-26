package com.zentao.publish.service.svn.impl

import com.zentao.publish.dao.IProjectDao
import com.zentao.publish.dao.IUserDao
import com.zentao.publish.extensions.splitRemoveEmpty
import com.zentao.publish.service.svn.ISvnService
import com.zentao.publish.viewmodel.SvnCommitInput
import com.zentao.publish.viewmodel.SvnList
import org.springframework.stereotype.Service
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import javax.annotation.Resource
import kotlin.io.path.Path

@Service
class DefaultSvnServiceImpl : ISvnService {

    @Resource
    private lateinit var _userDao: IUserDao

    @Resource
    private lateinit var _projectDao: IProjectDao

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
            val path = Path(System.getenv("appdata"), "publish", project.name!!, version)
            val result =
                exec("svn checkout \"${publishPath}\" \"${path}\" --username ${user.username} --password ${user.password}")
            if (result.any { p -> p.startsWith("Checked") }) {
                return "版本已创建完毕，请将文件存放至 $path 然后调用 Commit 接口"
            }
        }
        return output.joinToString()
    }

    override fun commit(input: SvnCommitInput): String {
        val project = _projectDao.getById(input.projectId) ?: throw  NullPointerException()
        val user = _userDao.getById(project.userId!!) ?: throw  NullPointerException()
        val version = if( !input.version.isNullOrEmpty()) input.version else {
            list(input.projectId).maxByOrNull { p -> p.revision.toInt() }?.entryName
        } ?: throw NullPointerException()

        val path = Path(System.getenv("appdata"), "publish", project.name!!, version)

        val addOutput = exec("svn add \"${path}\" --force")
        val output = exec("svn commit -m \"Commit\" \"${path}\" --username ${user.username} --password ${user.password}")
        return output.joinToString("\r\n")
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
}