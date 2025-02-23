package com.longfish.collabai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "search.display")
public class SearchDisplayLengthProperties {

    private Integer preLength;

    private Integer postLength;

}
