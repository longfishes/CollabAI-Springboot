package com.longfish.collabai.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AISessionListVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Long total;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> rows;
}
