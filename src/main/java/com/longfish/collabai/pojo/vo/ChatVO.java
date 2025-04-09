package com.longfish.collabai.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "大模型的回答")
    private String resp;

}
