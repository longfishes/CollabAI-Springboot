package com.longfish.collabai.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentSearchVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "14")
    private Long id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "title")
    private String title;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "content")
    private String content;
}
