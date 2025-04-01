package com.longfish.collabai.service;

public interface AIMessageService {

    void summarizeMeeting(String meetingId);

    String syncAiMessage(String meetingId);

    String getAIToken();
}
