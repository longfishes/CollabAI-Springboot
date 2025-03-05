package com.longfish.collabai.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.longfish.collabai.constant.CommonConstant.*;

@Configuration
public class OpenApiConfig {

    @Value("${project.version}")
    private String version;

    @Bean
    public OpenAPI springOpenApi() {
        // 创建联系人信息
        Contact contact = new Contact()
                .name("longfish")
                .email("longfishes@qq.com");

        // 创建API基本信息
        Info info = new Info().title("CollabAI 系统接口文档")
                .description("\"服务外包\" 【A18】 西湖论剑大会AI+会议助手智能体应用开发 【CollabAI】 接口文档")
                .version(version)
                .contact(contact);

        // 配置全局token认证
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(TOKEN_NAME)
                .description(HEADER_ADVICE);

        return new OpenAPI()
                .info(info)
                .components(new Components()
                        .addSecuritySchemes(TOKEN_NAME, securityScheme)
                        .addParameters(TOKEN_NAME, new Parameter()
                                .in("header")
                                .name(TOKEN_NAME)
                                .description(HEADER_ADVICE)
                                .required(true)
                                .schema(new StringSchema().example(HEADER_VAR))))
                .addSecurityItem(new SecurityRequirement().addList(TOKEN_NAME));
    }
}
