package com.longfish.collabai.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "3", description = "1-创建者holder 2-操作者operator 3-参与者participants")
    private Integer authType;
}
