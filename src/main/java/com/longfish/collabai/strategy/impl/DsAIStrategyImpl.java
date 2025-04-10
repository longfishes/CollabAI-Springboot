package com.longfish.collabai.strategy.impl;

import com.longfish.collabai.strategy.AIStrategy;
import com.longfish.collabai.util.DsRequestUtil;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("dsAIStrategyImpl")
public class DsAIStrategyImpl implements AIStrategy {

    @Autowired
    private DsRequestUtil dsRequestUtil;

    @Override
    public String summarySth(String message) {
        return dsRequestUtil.summarizeSth(message);
    }

    @Override
    public String chat(String content) {
        return dsRequestUtil.chat(content);
    }

    @Override
    public String chatWithHistory(List<Map<String, String>> history) {
        return dsRequestUtil.callDeepSeekApi(history);
    }

    @Override
    public String chatStream(Session session, List<Map<String, String>> history) {
        return dsRequestUtil.chatStream(session, history);
    }

}
