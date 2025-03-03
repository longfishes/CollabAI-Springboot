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
public class ParticipantsEditDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "3", description = "用户id")
    private Long userId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
            example = "3", description = "1-创建者holder 2-操作者operator 3-参与者participants")
    private Integer authType;
}
