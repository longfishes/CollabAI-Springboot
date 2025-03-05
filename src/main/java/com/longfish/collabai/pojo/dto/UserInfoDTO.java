package com.longfish.collabai.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.longfish.collabai.constant.CommonConstant.DATE_PATTERN;

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
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate birthday;

    @Schema(example = "广东")
    private String location;
}
