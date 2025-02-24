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
public class LambdaLoginDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "tt", description = "username/email/phone")
    private String username;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private String password;
}
