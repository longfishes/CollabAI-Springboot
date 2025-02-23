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
public class UserInfoDTO {

    @Schema(example = "hello")
    private String nickname;

    @Schema(example = "https://static.longfish.site/tt")
    private String avatar;

    @Schema(example = "info")
    private String info;

    @Schema(description = "1-男 2-女 3-未设", example = "1")
    private Integer gender;

    @Schema(example = "2000-01-01")
    private String birthday;

    @Schema(example = "广东")
    private String location;
}
