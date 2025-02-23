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
public class PasswordEditDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private String oldPassword;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "321")
    private String newPassword;
}
