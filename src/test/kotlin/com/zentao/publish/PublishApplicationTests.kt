package com.zentao.publish

import com.zentao.publish.service.mail.IMailService
import com.zentao.publish.viewmodel.MailSendInfo
import org.apache.poi.hwpf.HWPFDocument
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.Key
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

@SpringBootTest
class PublishApplicationTests {

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

    @Test
    fun encrypt() {
        val content = "123456"

        val encrypt = Base64.getEncoder().encodeToString(content.toByteArray())

        val decrypt = String(Base64.getDecoder().decode(encrypt))

        assert(content == decrypt)
    }
}
