package com.longfish.collabai.strategy.impl;

import com.longfish.collabai.strategy.AIStrategy;
import com.longfish.collabai.util.HengRequestUtil;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("hengAIStrategyImpl")
public class HengAIStrategyImpl implements AIStrategy {

    @Autowired
    private HengRequestUtil hengRequestUtil;

    @Override
    public String summarySth(String message) {
        return hengRequestUtil.summarySth(message);
    }

    @Override
    public String chat(String content) {
        return hengRequestUtil.chat(content);
    }

    @Override
    public String chatWithHistory(List<Map<String, String>> history) {
        return hengRequestUtil.callHengApi(history);
    }

    @Override
    public String chatStream(Session session, List<Map<String, String>> history) {
        return null;
    }

}
