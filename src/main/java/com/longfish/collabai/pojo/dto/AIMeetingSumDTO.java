package com.longfish.collabai.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AIMeetingSumDTO {

    private String id;

    private String mdContent;

    private String speechText;

    private String AiSummary;

}
