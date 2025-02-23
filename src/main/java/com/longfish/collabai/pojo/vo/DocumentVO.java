package com.longfish.collabai.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static com.longfish.collabai.constant.CommonConstant.PATTERN;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class DocumentVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "14")
    private Long id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "0-不可编辑 1-可编辑", example = "1")
    private Boolean editable;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "title")
    private String title;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "https://static.longfish.site/tt")
    private String cover;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ttAbs")
    private String docAbstract;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "content")
    private String content;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "/")
    private String path;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Boolean isTop;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Boolean isDeleted;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Boolean status;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2000-01-01 00:00:00")
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime createTime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2000-01-01 00:00:00")
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime updateTime;
}
