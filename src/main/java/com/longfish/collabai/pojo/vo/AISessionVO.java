package com.longfish.collabai.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AISessionVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "8df38f11-2329-4425-a474-3ebe089eceb7")
    private String sessionId;
}
