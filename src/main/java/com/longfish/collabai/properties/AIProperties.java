package com.longfish.collabai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AIProperties {

    private String appKey;

    private String appSecret;

    private String baseUrl;

    private String wsBaseUrl;

}
