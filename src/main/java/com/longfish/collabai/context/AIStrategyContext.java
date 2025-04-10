package com.longfish.collabai.context;

import com.longfish.collabai.strategy.AIStrategy;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.longfish.collabai.enums.AIModeEnum.getStrategy;

@Service
public class AIStrategyContext {

    @Value("${ai.mode}")
    private String aiMode;

    @Autowired
    private Map<String, AIStrategy> aiStrategyMap;

    public String execChatStream(Session session, List<Map<String, String>> history) {
        return aiStrategyMap.get(getStrategy(aiMode)).chatStream(session, history);
    }

    public String execSummarizeSth(String message) {
        return aiStrategyMap.get(getStrategy(aiMode)).summarySth(message);
    }

    public String execChat(String content) {
        return aiStrategyMap.get(getStrategy(aiMode)).chat(content);
    }

    public String execChatWithHistory(List<Map<String, String>> history) {
        return aiStrategyMap.get(getStrategy(aiMode)).chatWithHistory(history);
    }

}
