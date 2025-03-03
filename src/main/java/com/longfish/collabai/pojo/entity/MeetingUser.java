package com.longfish.collabai.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

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
@TableName("meeting_user")
@Builder
public class MeetingUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 7430921L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String meetingId;

    private Long userId;

    /*
        1-创建者holder 2-操作者operator 3-参与者participants
     */
    private Integer authType;
}
