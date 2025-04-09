package com.longfish.collabai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.ds")
public class DeepSeekProperties {

    private String baseUrl;

    private String apiKey;

}
