package com.longfish.collabai.strategy;

import com.longfish.collabai.pojo.vo.DocumentSearchVO;

import java.util.List;

public interface SearchStrategy {

    List<DocumentSearchVO> searchDocument(String keywords);

}
