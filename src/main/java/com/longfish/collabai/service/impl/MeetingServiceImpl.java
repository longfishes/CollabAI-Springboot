package com.longfish.collabai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longfish.collabai.context.BaseContext;
import com.longfish.collabai.mapper.MeetingMapper;
import com.longfish.collabai.pojo.dto.MeetingDTO;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.service.IMeetingService;
import com.longfish.collabai.util.MD5Util;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    @Override
    public void createNew(MeetingDTO meetingDTO) {
        Long currentId = BaseContext.getCurrentId();
        String currentName = BaseContext.getCurrentName();
        Meeting meeting = BeanUtil.copyProperties(meetingDTO, Meeting.class);
        meeting.setHolderId(currentId)
                .setHolderName(currentName)
                .setCreateTime(LocalDateTime.now());
        meeting.setId(MD5Util.gen(meeting));
        save(meeting);
    }
}
