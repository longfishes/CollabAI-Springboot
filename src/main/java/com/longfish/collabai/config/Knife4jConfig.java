package com.longfish.collabai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Value("${project.version}")
    private String version;

    @Bean
    public OpenAPI springOpenApi() {
        Contact contact = new Contact()
                .name("longfish")
                .email("longfishes@qq.com");
        Info info = new Info().title("CollabAI 系统接口文档")
                .description("”服务外包“ 【A18】 西湖论剑大会AI+会议助手智能体应用开发 【CollabAI】 接口文档")
                .version(version)
                .contact(contact);
        return new OpenAPI().info(info);
    }
}
