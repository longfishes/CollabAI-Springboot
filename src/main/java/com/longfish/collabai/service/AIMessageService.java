package com.longfish.collabai.service;

import com.longfish.collabai.pojo.dto.ChatDTO;
import com.longfish.collabai.pojo.vo.ChatVO;

public interface AIMessageService {

    void summarizeMeeting(String meetingId);

    String syncAiMessage(String meetingId);

    String getAIToken();

    ChatVO chat(ChatDTO chatDTO);
}
