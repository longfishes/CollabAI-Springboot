package com.longfish.collabai.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static com.longfish.collabai.constant.CommonConstant.MEETING_PATTERN;
import static com.longfish.collabai.constant.CommonConstant.PATTERN;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class MeetingVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "c198d1c782b707d8859968819d14d687", description = "会议号")
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ai交流", description = "会议主题")
    private String title;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "登录用户", description = "主持人")
    private String holderName;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com/img", description = "主持人头像")
    private String holderAvatar;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean isHolder;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2025-04-03 09:30", description = "会议开始时间")
    @JsonFormat(pattern = MEETING_PATTERN)
    private LocalDateTime startTime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2025-04-03 10:30", description = "会议结束时间")
    @JsonFormat(pattern = MEETING_PATTERN)
    private LocalDateTime endTime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "https://example.com/file", description = "封面图片url")
    private String coverImg;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "今天，会议的主题是", description = "语音转写内容")
    private String speechText;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "### 标题", description = "会议md内容")
    private String mdContent;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "### 标题", description = "会议ai总结")
    private String aiSummary;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2025-03-03 10:32:38", description = "创建时间")
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime createTime;

}
