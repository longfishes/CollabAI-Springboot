package com.longfish.collabai.strategy.impl;

import com.longfish.collabai.strategy.AIStrategy;
import com.longfish.collabai.util.DsRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
