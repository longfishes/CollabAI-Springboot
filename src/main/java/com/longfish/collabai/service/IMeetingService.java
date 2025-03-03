package com.longfish.collabai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.longfish.collabai.pojo.dto.MeetingDTO;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.pojo.vo.MeetingAbsVO;
import com.longfish.collabai.pojo.vo.MeetingVO;

import java.util.List;

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

    List<MeetingAbsVO> listMeetings();

    MeetingVO detail(String id);
}
