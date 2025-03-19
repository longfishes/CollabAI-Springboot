package com.longfish.collabai.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpeechVO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "语音识别结果")
    private String[] speechText;
}
