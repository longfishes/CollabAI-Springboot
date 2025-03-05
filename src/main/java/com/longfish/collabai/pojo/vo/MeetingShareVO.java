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
public class MeetingShareVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "c198d1c782b707d8859968819d14d687", description = "会议号")
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ai交流", description = "会议主题")
    private String title;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "登录用户", description = "主持人")
    private String holderName;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com/img", description = "主持人头像")
    private String holderAvatar;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "false", description = "是否已经加入")
    private Boolean isJoined;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2025-04-03 09:30:00", description = "会议开始时间")
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime startTime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2025-04-03 10:30:00", description = "会议结束时间")
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime endTime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "https://example.com/file", description = "封面图片url")
    private String coverImg;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2025-03-03 10:32:38", description = "创建时间")
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime createTime;

}
