package com.longfish.collabai.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.longfish.collabai.constant.CommonConstant.DATE_PATTERN;
import static com.longfish.collabai.constant.CommonConstant.PATTERN;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "tt")
    private String username;

    @Schema(example = "longfishes@qq.com")
    private String email;

    @Schema(example = "19000000000")
    private String phone;

    @Schema(example = "hello")
    private String nickname;

    @Schema(example = "https://static.longfish.site/auatar.users/aa.jpg")
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

    @Schema(example = "120.9.130.24")
    private String ipAddress;

    @Schema(example = "中国|广东省|湛江|联通")
    private String ipSource;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2000-01-01 00:00:00")
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime createTime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2000-01-01 00:00:00")
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime updateTime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2000-01-01 00:00:00")
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime lastLoginTime;
}
