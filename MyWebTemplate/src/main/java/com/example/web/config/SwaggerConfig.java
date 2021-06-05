package com.example.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    @Bean
    public Docket getDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("api")
                .apiInfo(new ApiInfo("系统API文档", "欢迎各位前端工程师查看", "v1.0", "",
                        new Contact("", "", ""),
                        "Apache 2.0", "", new ArrayList<>()))
                .enable(true)
                .select()
                .paths(PathSelectors.ant("/api/**"))
                .build();
    }
}
