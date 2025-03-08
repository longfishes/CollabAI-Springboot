package com.longfish.collabai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rtasr")
public class TtlProperties {

    private String appId;

    private String secretKey;

    private String wsBaseUrl;

    private String httpBaseUrl;

}
