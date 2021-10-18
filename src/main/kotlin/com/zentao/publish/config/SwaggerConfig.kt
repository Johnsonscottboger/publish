package com.zentao.publish.config

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j
import io.swagger.annotations.ApiOperation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc

@Configuration
@EnableSwagger2WebMvc
class SwaggerConfig {

    @Bean
    fun createRestApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .groupName("产品发布服务")
            .pathMapping("/")
            .apiInfo(
                ApiInfoBuilder()
                    .title("产品发布服务接口")
                    .description("产品发布服务接口")
                    .contact(Contact("王宁波", "", "wangnb@wuhanins.com"))
                    .version("1.0")
                    .build()
            )
            .select()
            .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation::class.java))
            .paths(PathSelectors.any())
            .build()
    }
}