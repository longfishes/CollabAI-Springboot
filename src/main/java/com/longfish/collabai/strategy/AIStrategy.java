package com.longfish.collabai.strategy;

import jakarta.websocket.Session;

import java.util.List;
import java.util.Map;

public interface AIStrategy {

    String summarySth(String message);

    String chat(String content);

    String chatWithHistory(List<Map<String, String>> history);

    String chatStream(Session session, List<Map<String, String>> history);
}
