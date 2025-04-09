package com.longfish.collabai.strategy.impl;

import com.longfish.collabai.strategy.AIStrategy;
import com.longfish.collabai.util.HengRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return null;
    }

}
