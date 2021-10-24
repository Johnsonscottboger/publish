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
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

@SpringBootTest
class PublishApplicationTests {

    @Autowired
    private lateinit var _mailService: IMailService

    @Test
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
