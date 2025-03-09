package com.longfish.collabai.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.longfish.collabai.constant.CommonConstant.DATE_PATTERN;
import static com.longfish.collabai.constant.CommonConstant.DEFAULT_AVATAR;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MeetingUserVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "用户id")
    private Long userId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "3", description = "1-创建者holder 2-操作者operator 3-参与者participants")
    private Integer authType;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "tt")
    private String username;

    @Schema(example = "longfishes@qq.com")
    private String email;

    @Schema(example = "19000000000")
    private String phone;

    @Schema(example = "hello")
    private String nickname;

    @Schema(example = DEFAULT_AVATAR)
    private String avatar;

    @Schema(example = "info")
    private String info;

    @Schema(description = "1-男 2-女 3-未设", example = "hello")
    private Integer gender;

    @Schema(example = "2000-01-01")
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate birthday;

    @Schema(example = "广东")
    private String location;

}
