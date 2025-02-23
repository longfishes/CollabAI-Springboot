package com.longfish.collabai.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

import static com.longfish.collabai.constant.CommonConstant.PATTERN;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DocumentAbstractVO implements Serializable {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    private Long id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "title")
    private String title;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "https://static.longfish.site/tt")
    private String cover;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ttAbs")
    private String docAbstract;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "/")
    private String path;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "0-私有 1-公开", example = "0")
    private Boolean status;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2000-01-01 00:00:00")
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime createTime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2000-01-01 00:00:00")
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime updateTime;
}
