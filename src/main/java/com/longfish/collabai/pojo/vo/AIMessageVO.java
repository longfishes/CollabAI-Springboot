package com.longfish.collabai.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AIMessageVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "ai总结内容")
    private String aiSummary;
}
