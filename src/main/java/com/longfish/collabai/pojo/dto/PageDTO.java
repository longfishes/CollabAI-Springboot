package com.longfish.collabai.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

import static com.longfish.collabai.constant.PageConstant.*;

@Data
public class PageDTO implements Serializable {

    @Schema(example = "1")
    private Long pageNo = DEFAULT_PAGE_NO;

    @Schema(example = "10")
    private Long pageSize = DEFAULT_PAGE_SIZE;

    @Schema(example = "update_time")
    private String sortBy = DEFAULT_SORTED_BY;

    @Schema(example = "true")
    private Boolean isAsc = DEFAULT_IS_ASC;

}
