package com.longfish.collabai.mapper;

import com.longfish.collabai.pojo.entity.MeetingUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longfish.collabai.pojo.vo.MeetingUserVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author longfish
 * @since 2025-03-03
 */
public interface MeetingUserMapper extends BaseMapper<MeetingUser> {

    List<MeetingUserVO> selectMeetingUsersByMeetingId(String id);
}
