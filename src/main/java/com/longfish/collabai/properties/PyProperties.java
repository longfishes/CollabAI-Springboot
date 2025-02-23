package com.longfish.collabai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "py")
public class PyProperties {

    private String baseUrl;

    private String ocrUrl;

    private String ocrPath;

    private String headerName;

    private String accessKey;
}
