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
public class PhoneForgotDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "180XXXXXXXX")
    private String phone;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "321123")
    private String password;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "114514")
    private String code;
}
