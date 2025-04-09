package com.longfish.collabai.strategy.impl;

import com.longfish.collabai.pojo.vo.DocumentSearchVO;
import com.longfish.collabai.properties.SearchDisplayLengthProperties;
import com.longfish.collabai.strategy.SearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("mySqlSearchStrategyImpl")
public class MySqlSearchStrategyImpl implements SearchStrategy {

    @Autowired
    private SearchDisplayLengthProperties lengthProperties;

    @Override
    public List<DocumentSearchVO> search(String keywords) {
        return null;
    }

}
