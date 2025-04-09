package com.longfish.collabai.strategy;

import java.util.List;
import java.util.Map;

public interface AIStrategy {

    String summarySth(String message);

    String chat(String content);

    String chatWithHistory(List<Map<String, String>> history);

}
