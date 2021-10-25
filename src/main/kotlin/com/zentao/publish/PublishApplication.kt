package com.zentao.publish

import com.fasterxml.jackson.databind.DeserializationFeature
import org.mybatis.spring.annotation.MapperScan
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import java.text.SimpleDateFormat
import java.util.*

@SpringBootApplication
@EnableScheduling
@MapperScan(basePackages = ["com.zentao.publish.dao"])
class PublishApplication : WebMvcConfigurer {

    @Value("\${spring.jackson.date-format}")
    private lateinit var dateFormatPattern: String

    @Value("\${spring.jackson.time-zone}")
    private lateinit var timezone: String

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(0, MappingJackson2HttpMessageConverter())
    }

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        val converter = MappingJackson2HttpMessageConverter()
        val objectMapper = converter.objectMapper

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        if (dateFormatPattern.isNotEmpty())
            objectMapper.dateFormat = SimpleDateFormat(dateFormatPattern)
        if (timezone.isNotEmpty())
            objectMapper.setTimeZone(TimeZone.getTimeZone(timezone))
        converter.objectMapper = objectMapper
        converters.add(0, converter)
    }
}

fun main(args: Array<String>) {
    runApplication<PublishApplication>(*args)
}
