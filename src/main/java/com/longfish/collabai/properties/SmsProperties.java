package com.longfish.collabai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String signName;

    private String templateCode;
}
