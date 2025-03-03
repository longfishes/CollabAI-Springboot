package com.longfish.collabai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longfish.collabai.context.BaseContext;
import com.longfish.collabai.mapper.MeetingMapper;
import com.longfish.collabai.mapper.MeetingUserMapper;
import com.longfish.collabai.pojo.dto.MeetingDTO;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.pojo.entity.MeetingUser;
import com.longfish.collabai.pojo.vo.MeetingAbsVO;
import com.longfish.collabai.pojo.vo.MeetingUserVO;
import com.longfish.collabai.pojo.vo.MeetingVO;
import com.longfish.collabai.service.IMeetingService;
import com.longfish.collabai.service.IMeetingUserService;
import com.longfish.collabai.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author longfish
 * @since 2025-03-03
 */
@Service
public class MeetingServiceImpl extends ServiceImpl<MeetingMapper, Meeting> implements IMeetingService {

    @Autowired
    private IMeetingUserService meetingUserService;

    @Autowired
    private MeetingMapper meetingMapper;

    @Autowired
    private MeetingUserMapper meetingUserMapper;

    @Override
    public void createNew(MeetingDTO meetingDTO) {
        Long currentId = BaseContext.getCurrentId();
        String currentName = BaseContext.getCurrentName();
        Meeting meeting = BeanUtil.copyProperties(meetingDTO, Meeting.class);
        meeting.setHolderId(currentId)
                .setHolderName(currentName)
                .setCreateTime(LocalDateTime.now());
        String meetingId = MD5Util.gen(meeting);
        meeting.setId(meetingId);
        save(meeting);

        meetingUserService.save(
            MeetingUser.builder()
                .meetingId(meetingId)
                .userId(currentId)
                .authType(3)
                .build()
        );
    }

    @Override
    public List<MeetingAbsVO> listMeetings() {
        Long currentId = BaseContext.getCurrentId();
        List<MeetingUser> meetingUserList = meetingUserService
                .lambdaQuery()
                .eq(MeetingUser::getUserId, currentId)
                .list();
        List<String> meetingIds = meetingUserList.stream().map(MeetingUser::getMeetingId).toList();
        List<Meeting> meetings = listByIds(meetingIds);

        LocalDateTime now = LocalDateTime.now();

        return meetings.stream().sorted((m1, m2) -> {
            boolean m1NotStarted = m1.getStartTime().isAfter(now);
            boolean m2NotStarted = m2.getStartTime().isAfter(now);

            if (m1NotStarted && m2NotStarted) {
                return m1.getStartTime().compareTo(m2.getStartTime());
            } else if (!m1NotStarted && !m2NotStarted) {
                return m2.getEndTime().compareTo(m1.getEndTime());
            } else {
                return m1NotStarted ? -1 : 1;
            }
        }).map(meeting -> {
            MeetingAbsVO vo = BeanUtil.copyProperties(meeting, MeetingAbsVO.class);
            vo.setIsHolder(currentId.equals(meeting.getHolderId()));
            return vo;
        }).toList();
    }

    @Override
    public List<Meeting> listByIds(Collection<? extends Serializable> idList) {
        if (idList == null || idList.size() == 0) {
            return new ArrayList<>();
        }
        return super.listByIds(idList);
    }

    @Override
    public MeetingVO detail(String id) {
        Long currentId = BaseContext.getCurrentId();
        Meeting meeting = getById(id);

        List<MeetingUserVO> participants = meetingUserMapper.selectMeetingUsersByMeetingId(id);

        return BeanUtil.copyProperties(meeting, MeetingVO.class)
                .setIsHolder(currentId.equals(meeting.getHolderId()))
                .setParticipants(participants);
    }
}
