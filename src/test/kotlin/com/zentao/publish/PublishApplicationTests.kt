package com.zentao.publish

import com.zentao.publish.service.mail.IMailService
import com.zentao.publish.viewmodel.MailSendInfo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PublishApplicationTests {

    @Autowired
    private lateinit var _mailService: IMailService

    @Test
    fun send() {
        val info = MailSendInfo(
            productName = "2021D01 英思基础数据中台",
            productPublishPath = "http://psvn.wuhanins.com/svn/产品发布库/2021D01 基础数据中台",
            projectName = "TB水电站绿色智能建造",
            projectVersion = "1.01.{00i}.{yyMMdd}",
            projectPublishPath = "http://psvn.wuhanins.com/svn/2020/2020Z11 TB水电站绿色智能建造平台、通用管理系统建设及维护项目/工作库/2产品生产/2.4软件测试/2.4.4提测版本/",
            zentaoAddress = "http://zentao.wuhanins.com:88/zentao/my/",
            description = ""
        )
        _mailService.send("wangnb@wuhanins.com", info)
    }
}
