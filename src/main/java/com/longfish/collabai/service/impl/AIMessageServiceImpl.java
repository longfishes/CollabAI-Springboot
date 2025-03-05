package com.longfish.collabai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.longfish.collabai.constant.RabbitMQConstant;
import com.longfish.collabai.context.BaseContext;
import com.longfish.collabai.enums.StatusCodeEnum;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.pojo.dto.AIMeetingSumDTO;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.service.AIMessageService;
import com.longfish.collabai.service.IMeetingService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AIMessageServiceImpl implements AIMessageService {

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void summarizeMeeting(String meetingId) {
        if (meetingId == null) {
            throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);
        }
        Meeting meeting = meetingService.getById(meetingId);

        if (meeting == null) {
            throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);
        }

        if (!BaseContext.getCurrentId().equals(meeting.getHolderId())) {
            throw new BizException(StatusCodeEnum.FORBIDDEN);
        }

        AIMeetingSumDTO dto = BeanUtil.copyProperties(meeting, AIMeetingSumDTO.class);

        rabbitTemplate.convertAndSend(
                RabbitMQConstant.AI_SUMMARIZE_EXCHANGE,
                "*",
                new Message(JSON.toJSONBytes(dto), new MessageProperties())
        );
    }
}
