package com.longfish.collabai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.longfish.collabai.pojo.dto.MeetingDTO;
import com.longfish.collabai.pojo.entity.Meeting;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author longfish
 * @since 2025-03-03
 */
public interface IMeetingService extends IService<Meeting> {

    void createNew(MeetingDTO meetingDTO);
}
