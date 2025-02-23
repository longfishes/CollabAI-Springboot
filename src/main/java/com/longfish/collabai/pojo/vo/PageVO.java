package com.longfish.collabai.pojo.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class PageVO<T> implements Serializable {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Long total;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<T> rows;

    public PageVO(Page<T> page) {
        rows = page.getRecords();
        total = page.getTotal();
    }

    public PageVO(List<T> records) {
        rows = records;
        total = (long)records.size();
    }
}
