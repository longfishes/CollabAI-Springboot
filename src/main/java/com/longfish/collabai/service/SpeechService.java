package com.longfish.collabai.service;

public interface SpeechService {

    void recognizeSpeech(byte[] audioData, String meetingId);

    String syncSpeechText(String meetingId);
}
