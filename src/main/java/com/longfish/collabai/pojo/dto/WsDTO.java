package com.longfish.collabai.pojo.dto;

import jakarta.websocket.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class WsDTO {

    private Session session;

    private String meetingId;

    private Long userId;

    private String nickName;
}
