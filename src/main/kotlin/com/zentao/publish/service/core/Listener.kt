package com.zentao.publish.service.core

import com.zentao.publish.condition.HistoryPageCondition
import com.zentao.publish.dao.IProductDao
import com.zentao.publish.dao.IProjectDao
import com.zentao.publish.dao.ISubscribeDao
import com.zentao.publish.dao.IUserDao
import com.zentao.publish.entity.PubUser
import com.zentao.publish.event.DelayUpdateEvent
import com.zentao.publish.event.SvnUpdateEvent
import com.zentao.publish.eventbus.IEventBus
import com.zentao.publish.extensions.splitRemoveEmpty
import com.zentao.publish.service.IMapService
import com.zentao.publish.service.MapperService
import com.zentao.publish.service.history.IHistoryService
import com.zentao.publish.service.mail.IMailService
import com.zentao.publish.service.svn.ISvnService
import com.zentao.publish.util.Encrypt
import com.zentao.publish.viewmodel.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import javax.annotation.Resource
import kotlin.io.path.*

@Component
class Listener {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Resource
    private lateinit var userDao: IUserDao

    @Resource
    private lateinit var productDao: IProductDao

    @Resource
    private lateinit var projectDao: IProjectDao

    @Resource
    private lateinit var subscribeDao: ISubscribeDao

    @Resource
    private lateinit var historyService: IHistoryService

    @Resource
    private lateinit var svnService: ISvnService

    @Resource
    private lateinit var mailService: IMailService

    @Resource
    private lateinit var eventBus: IEventBus

    @Scheduled(fixedRate = 600000, initialDelay = 10000)
    fun listenSvn() {
        eventBus.subscribe(Subscriber::class.java)
        log.info("开始检查更新")
        try {
            val userList = userDao.getAll()
            val productList = productDao.getAll()
            val projectList = projectDao.getAll()
            val subscribeList = subscribeDao.getAll()

            log.info("共查询到user-${userList.count()}个, product-${productList.count()}个, project-${projectList.count()}个, subscribe-${subscribeList.count()}个")

            for (product in productList) {
                log.info("当前产品:${product.id} ${product.name}")
                val subscribes = subscribeList.filter { p -> p.productId == product.id }
                log.info("\t订阅数量:${subscribes.count()}")
                for (subscribe in subscribes) {
                    try {
                        val project = projectList.find { p -> p.id == subscribe.projectId } ?: continue
                        val user = userList.find { p -> p.id == project.userId } ?: continue
                        val publishPath = "${product.publishPath}/${subscribe.productSubPath}"
                        log.info("\t订阅项目:${project.id} ${project.name}")
                        val list =
                            svnService.exec(
                                "svn list \"${publishPath}\" --verbose --username ${user.username} --password ${
                                    Encrypt.decrypt(
                                        user.password!!
                                    )
                                }"
                            ).drop(
                                1
                            ).map { p ->
                                val split = p.splitRemoveEmpty(" ")
                                SvnList(split[0], split[1], split.elementAtOrElse(6) { "" }.removeSuffix("/"))
                            }

                        val lastVersion = list.maxByOrNull { p -> p.revision.toInt() } ?: continue
                        log.info("\t产品最新版本:${lastVersion.entryName}")

                        val currentVersion = subscribe.lastProductVersion
                        log.info("\t项目最新版本:${currentVersion}")
                        if (checkNeedUpdate(
                                user,
                                publishPath,
                                subscribe.productId!!,
                                subscribe.projectId!!,
                                lastVersion.entryName,
                                currentVersion
                            )
                        ) {
                            log.info("当前项目需要更新")
                            eventBus.publish(
                                SvnUpdateEvent(
                                    product = MapperService.map(product, Product::class)!!,
                                    project = MapperService.map(project, Project::class)!!,
                                    subscribe = MapperService.map(subscribe, Subscribe::class)!!,
                                    lastVersion = lastVersion
                                )
                            )
                        } else {
                            log.info("\t当前项目不需要更新")
                        }
                    } catch (error: Throwable) {
                        log.error("检查更新异常: $subscribe", error)
                        mailService.errorReport("订阅${subscribe}更新异常: " + error.message, error)
                    }
                }
            }
        } catch (error: Throwable) {
            log.error("检查更新异常", error)
            mailService.errorReport(error.message!!, error)
            log.info("检查更新异常报告已发送")
        } finally {
            log.info("更新完毕")
        }
    }

    /**
     * 每周一上午9点执行
     */
    @Scheduled(cron = "0 0 9 ? * MON")
    fun listenDelayQueue() {
        log.info("开始检查更新")
        try {
            val projectList = projectDao.getAll()

            for (project in projectList) {
                val history = historyService.getPage(HistoryPageCondition(projectId = project.id, published = 0))
                if (history.data.any()) {
                    eventBus.publish(
                        DelayUpdateEvent(
                            project = MapperService.map(project, Project::class)!!,
                            histories = history.data
                        )
                    )
                }
            }
        } catch (error: Throwable) {
            log.error("检查更新异常", error)
            mailService.errorReport(error.message!!, error)
        } finally {
            log.info("更新完毕")
        }
    }

    private fun checkNeedUpdate(
        user: PubUser,
        publishPath: String,
        productId: String,
        projectId: String,
        productVersion: String?,
        projectVersion: String?
    ): Boolean {
        if (projectVersion.isNullOrEmpty()) return true
        if (productVersion.isNullOrEmpty()) return false
        val productFile = File(productVersion).nameWithoutExtension
        val projectFile = File(projectVersion).nameWithoutExtension

        //判断文件最新的日志是否为新增, 如果不是新增则不更新
        val logs = svnService.exec(
            "svn log -v \"${publishPath}/${productVersion}\" --username ${user.username} --password ${
                Encrypt.decrypt(
                    user.password!!
                )
            }"
        )

        if (logs.any()) {
            val log = Regex("([AM]).*").find(logs.joinToString())
            if (log != null && log.value.startsWith("M"))
                return false
        }

        //检查历史记录, 如果历史记录中已发布, 则不再更新
        val histories = historyService.getPage(HistoryPageCondition(productId = productId, projectId = projectId))
        if (histories.data.any { p -> p.productVersion == productVersion })
            return false

        return productFile != projectFile
    }
}