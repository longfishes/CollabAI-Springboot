package com.longfish.collabai.strategy.impl;

import com.longfish.collabai.pojo.vo.DocumentSearchVO;
import com.longfish.collabai.strategy.SearchStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("esSearchStrategyImpl")
public class EsSearchStrategyImpl implements SearchStrategy {

    @Override
    public List<DocumentSearchVO> search(String keywords) {
        return null;
    }
}
