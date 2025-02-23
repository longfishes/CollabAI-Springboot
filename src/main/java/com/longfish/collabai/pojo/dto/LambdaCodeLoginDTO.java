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
public class LambdaCodeLoginDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "longfishes@qq.com")
    private String username;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "114514")
    private String code;
}
