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
public class RegDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "tt")
    private String username;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example ="19000000000")
    private String phone;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "114514")
    private String code;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private String password;
}
