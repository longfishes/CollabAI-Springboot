package com.longfish.collabai.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            description = "访问上传文件的url",
            example = "https://static.longfish.site/avatar/users/tt.png")
    private String url;
}
