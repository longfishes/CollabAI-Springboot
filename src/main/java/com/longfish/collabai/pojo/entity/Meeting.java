package com.longfish.collabai.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author longfish
 * @since 2025-03-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("meeting")
public class Meeting implements Serializable {

    @Serial
    private static final long serialVersionUID = 18409321L;

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    private String title;

    private Long holderId;

    private String holderName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String mdContent;

    private String aiSummary;

    private String coverImg;

    private LocalDateTime createTime;


}
