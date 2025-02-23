package com.longfish.collabai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "page")
public class PageProperties {

    private Long max;

    private Long defaultPageNo;

    private Long defaultPageSize;

    private Boolean defaultIsAsc;

    private String defaultSortBy;
}
