package com.longfish.collabai.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "你好", description = "对话内容")
    private String content;

}
