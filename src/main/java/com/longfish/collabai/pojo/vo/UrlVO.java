package com.longfish.collabai.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import static com.longfish.collabai.constant.CommonConstant.DEFAULT_COVER;

@Data
@Builder
public class UrlVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            description = "访问上传文件的url",
            example = DEFAULT_COVER)
    private String url;
}
