package com.longfish.collabai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longfish.collabai.constant.MeetingConstant;
import com.longfish.collabai.context.BaseContext;
import com.longfish.collabai.enums.StatusCodeEnum;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.mapper.MeetingMapper;
import com.longfish.collabai.mapper.MeetingUserMapper;
import com.longfish.collabai.pojo.dto.MeetingDTO;
import com.longfish.collabai.pojo.dto.MeetingEditDTO;
import com.longfish.collabai.pojo.dto.ParticipantsEditDTO;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.pojo.entity.MeetingUser;
import com.longfish.collabai.pojo.entity.User;
import com.longfish.collabai.pojo.vo.*;
import com.longfish.collabai.service.IMeetingService;
import com.longfish.collabai.service.IMeetingUserService;
import com.longfish.collabai.service.IUserService;
import com.longfish.collabai.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.longfish.collabai.constant.MeetingConstant.HOLDER;
import static com.longfish.collabai.constant.MeetingConstant.OPERATOR;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author longfish
 * @since 2025-03-03
 */
@Service
@Slf4j
public class MeetingServiceImpl extends ServiceImpl<MeetingMapper, Meeting> implements IMeetingService {

    @Autowired
    private IMeetingUserService meetingUserService;

    @Autowired
    private MeetingUserMapper meetingUserMapper;

    @Autowired
    private MeetingMapper meetingMapper;

    @Autowired
    private IUserService userService;

    @Override
    public void createNew(MeetingDTO meetingDTO) {
        Long currentId = BaseContext.getCurrentId();

        // 检查开始时间和结束时间的合法性
        LocalDateTime startTime = meetingDTO.getStartTime();
        LocalDateTime endTime = meetingDTO.getEndTime();
        LocalDateTime now = LocalDateTime.now();

        if (startTime.isBefore(now)) {
            throw new BizException(StatusCodeEnum.START_TIME_ERROR);
        }
        if (endTime.isBefore(startTime)) {
            throw new BizException(StatusCodeEnum.END_TIME_ERROR);
        }

        long diff = endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC);
        if (diff > MeetingConstant.MAX_LAST_TIME) {
            throw new BizException(StatusCodeEnum.MEETING_TOO_LONG);
        }

        Meeting meeting = BeanUtil.copyProperties(meetingDTO, Meeting.class);
        meeting.setHolderId(currentId)
                .setCreateTime(now);
        String meetingId = MD5Util.gen(meeting);
        meeting.setId(meetingId);
        save(meeting);

        meetingUserService.save(
            MeetingUser.builder()
                .meetingId(meetingId)
                .userId(currentId)
                .authType(HOLDER)
                .build()
        );
    }

    @Override
    public void edit(MeetingEditDTO meetingEditDTO) {
        String meetingId = meetingEditDTO.getId();
        if (meetingId == null) {
            throw new BizException("会议号不能为空");
        }
        Meeting meeting = getById(meetingId);
        if (meeting == null) throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);

        MeetingUser meetingUser = meetingUserService.lambdaQuery()
                .eq(MeetingUser::getUserId, BaseContext.getCurrentId())
                .eq(MeetingUser::getMeetingId, meetingEditDTO.getId())
                .one();

        Integer authType = meetingUser.getAuthType();
        if (!HOLDER.equals(authType) && !OPERATOR.equals(authType)) {
            throw new BizException(StatusCodeEnum.FORBIDDEN);
        }

        BeanUtil.copyProperties(meetingEditDTO, meeting);

        // 检查开始时间和结束时间的合法性
        LocalDateTime startTime = meeting.getStartTime();
        LocalDateTime endTime = meeting.getEndTime();
        LocalDateTime now = LocalDateTime.now();

        if (startTime.isBefore(now)) {
            throw new BizException(StatusCodeEnum.START_TIME_ERROR);
        }
        if (endTime.isBefore(startTime)) {
            throw new BizException(StatusCodeEnum.END_TIME_ERROR);
        }

        long diff = endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC);
        if (diff > MeetingConstant.MAX_LAST_TIME) {
            throw new BizException(StatusCodeEnum.MEETING_TOO_LONG);
        }

        updateById(meeting);
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
            Long holderId = meeting.getHolderId();
            return BeanUtil.copyProperties(meeting, MeetingAbsVO.class)
                    .setIsHolder(currentId.equals(holderId))
                    .setHolderName(userService.getById(holderId).getNickname());
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
        if (meeting == null) throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);

        User holder = userService.getById(meeting.getHolderId());

        return BeanUtil.copyProperties(meeting, MeetingVO.class)
                .setIsHolder(currentId.equals(meeting.getHolderId()))
                .setHolderName(holder.getNickname())
                .setHolderAvatar(holder.getAvatar());
    }

    @Override
    public List<MeetingUserVO> participants(String id) {
        return meetingUserMapper.selectMeetingUsersByMeetingId(id);
    }

    @Override
    public void editMember(String id, List<ParticipantsEditDTO> editDTOList) {
        Meeting meeting = getById(id);
        if (meeting == null) throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);

        if (!BaseContext.getCurrentId().equals(meeting.getHolderId())) {
            throw new BizException(StatusCodeEnum.FORBIDDEN);
        }
        final boolean[] flag = {false, false};
        editDTOList.forEach(e -> {
            if (e.getAuthType().equals(HOLDER)) {
                if (flag[0]) flag[1] = true;
                flag[0] = true;
            }
        });
        if (flag[1]) {
            throw new BizException("不能有多个创建者");
        }
        if (!flag[0]) {
            throw new BizException("至少有一个创建者");
        }

        meetingUserService.remove(
                new LambdaQueryWrapper<MeetingUser>().eq(MeetingUser::getMeetingId, id)
        );

        List<MeetingUser> meetingUsers = editDTOList.stream().map(participantsEditDTO ->
                BeanUtil.copyProperties(participantsEditDTO, MeetingUser.class)
                        .setMeetingId(id)).toList();

        meetingUserService.saveBatch(meetingUsers);
    }

    @Override
    public void join(String id) {
        Long currentId = BaseContext.getCurrentId();

        Meeting meeting = getById(id);
        if (meeting == null) throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);

        if (meetingUserService.lambdaQuery()
                .eq(MeetingUser::getUserId, currentId)
                .eq(MeetingUser::getMeetingId, id)
                .exists()) {
            throw new BizException("已经加入了会议");
        }

        if (LocalDateTime.now().isAfter(meeting.getEndTime())) {
            throw new BizException("会议已经结束了");
        }

        meetingUserService.save(
            MeetingUser.builder()
                    .meetingId(id)
                    .userId(currentId)
                    .authType(MeetingConstant.PARTICIPANT)
                    .build()
        );
    }

    @Override
    public void leave(String id) {
        Long currentId = BaseContext.getCurrentId();
        Meeting meeting = getById(id);
        if (meeting == null) throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);

        if (!currentId.equals(meeting.getHolderId())) {
            throw new BizException("请先转让会议所有权再退出");
        }
        meetingUserService.remove(
                new LambdaQueryWrapper<MeetingUser>()
                        .eq(MeetingUser::getMeetingId, id)
                        .eq(MeetingUser::getUserId, currentId)
        );
    }

    @Override
    public void del(String id) {
        Long currentId = BaseContext.getCurrentId();
        Meeting meeting = getById(id);
        if (meeting == null) throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);

        if (!currentId.equals(meeting.getHolderId())) {
            throw new BizException(StatusCodeEnum.FORBIDDEN);
        }
        removeById(id);
        meetingUserService.remove(
                new LambdaQueryWrapper<MeetingUser>().eq(MeetingUser::getMeetingId, id)
        );
    }

    @Override
    public void start(String id) {
        Meeting meeting = getById(id);
        if (meeting == null) throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);

        MeetingUser meetingUser = meetingUserService.lambdaQuery()
                .eq(MeetingUser::getUserId, BaseContext.getCurrentId())
                .eq(MeetingUser::getMeetingId, id)
                .one();

        Integer authType = meetingUser.getAuthType();
        if (!HOLDER.equals(authType) && !OPERATOR.equals(authType)) {
            throw new BizException(StatusCodeEnum.FORBIDDEN);
        }

        LocalDateTime startTime = meeting.getStartTime();
        LocalDateTime now = LocalDateTime.now();

        if (startTime.isBefore(now)) {
            throw new BizException("会议已经开始");
        }

        meeting.setStartTime(now);
        updateById(meeting);
    }

    @Override
    public void stop(String id) {
        Meeting meeting = getById(id);
        if (meeting == null) throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);

        MeetingUser meetingUser = meetingUserService.lambdaQuery()
                .eq(MeetingUser::getUserId, BaseContext.getCurrentId())
                .eq(MeetingUser::getMeetingId, id)
                .one();

        Integer authType = meetingUser.getAuthType();
        if (!HOLDER.equals(authType) && !OPERATOR.equals(authType)) {
            throw new BizException(StatusCodeEnum.FORBIDDEN);
        }

        LocalDateTime endTime = meeting.getEndTime();
        LocalDateTime now = LocalDateTime.now();

        if (endTime.isAfter(now)) {
            throw new BizException("会议已经结束");
        }

        meeting.setEndTime(now);
        updateById(meeting);
    }

    @Override
    public MeetingShareVO shareDetail(String meetingId) {
        Long currentId = BaseContext.getCurrentId();
        Meeting meeting = getById(meetingId);
        if (meeting == null) throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);

        User holder = userService.getById(meeting.getHolderId());

        boolean isJoined = meetingUserService.lambdaQuery()
                .eq(MeetingUser::getUserId, currentId)
                .eq(MeetingUser::getMeetingId, meetingId)
                .exists();

        return BeanUtil.copyProperties(meeting, MeetingShareVO.class)
                .setIsJoined(isJoined)
                .setHolderName(holder.getNickname())
                .setHolderAvatar(holder.getAvatar());
    }

    @Override
    public AuthVO auth(String meetingId) {
        MeetingUser meetingUser = meetingUserService.lambdaQuery()
                .eq(MeetingUser::getMeetingId, meetingId)
                .eq(MeetingUser::getUserId, BaseContext.getCurrentId())
                .one();
        if (meetingUser == null) throw new BizException(StatusCodeEnum.FORBIDDEN);
        return AuthVO.builder().authType(meetingUser.getAuthType()).build();
    }

    @Override
    public void setDefaultCover(String url) {
        meetingMapper.setDefaultCover(url);
    }
}
