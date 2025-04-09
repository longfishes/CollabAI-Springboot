package com.longfish.collabai.pojo.dto;

import jakarta.websocket.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class WsDTO {

    private Session session;

    private Long userId;

    private String nickName;

    private List<Map<String, String>> chatHistory;

}
