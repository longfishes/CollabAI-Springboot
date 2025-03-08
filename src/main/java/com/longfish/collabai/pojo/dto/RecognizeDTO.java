package com.longfish.collabai.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecognizeDTO {

    private String content;

    private String meetingId;

}
