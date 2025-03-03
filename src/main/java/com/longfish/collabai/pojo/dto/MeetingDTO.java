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
public class MeetingDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ai交流", description = "会议主题")
    private String title;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2025-04-03 09:30:00", description = "会议开始时间")
    private String startTime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2025-04-03 10:30:00", description = "会议结束时间")
    private String endTime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "### 标题", description = "会议md内容")
    private String mdContent;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "https://example.com/file", description = "封面图片url")
    private String coverImg;
}
