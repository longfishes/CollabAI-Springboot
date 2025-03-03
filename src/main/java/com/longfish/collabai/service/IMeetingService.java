package com.longfish.collabai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.longfish.collabai.pojo.dto.MeetingDTO;
import com.longfish.collabai.pojo.dto.MeetingEditDTO;
import com.longfish.collabai.pojo.dto.ParticipantsEditDTO;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.pojo.vo.MeetingAbsVO;
import com.longfish.collabai.pojo.vo.MeetingUserVO;
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

    void edit(MeetingEditDTO meetingEditDTO);

    List<MeetingAbsVO> listMeetings();

    MeetingVO detail(String id);

    List<MeetingUserVO> participants(String id);

    void editMember(String id, List<ParticipantsEditDTO> editDTOList);

    void join(String id);
}
