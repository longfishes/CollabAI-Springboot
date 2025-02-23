package com.longfish.collabai.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TitleVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "标题")
    private String title;
}
