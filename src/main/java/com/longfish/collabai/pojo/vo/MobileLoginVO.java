package com.longfish.collabai.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MobileLoginVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "YoNpoDuAljBOfUO7Ed3qGQ==")
    private String uid;
}
