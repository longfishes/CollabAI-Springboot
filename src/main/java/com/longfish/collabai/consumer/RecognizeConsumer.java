package com.longfish.collabai.consumer;

import com.alibaba.fastjson.JSON;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.pojo.dto.RecognizeDTO;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.service.IMeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.longfish.collabai.constant.RabbitMQConstant.RECOGNIZE_QUEUE;

@Slf4j
@Component
@RabbitListener(queues = RECOGNIZE_QUEUE)
public class RecognizeConsumer {

    @Autowired
    private IMeetingService meetingService;

    @RabbitHandler
    public void process(byte[] data) {
        RecognizeDTO recognizeDTO = JSON.parseObject(new String(data), RecognizeDTO.class);
        Meeting meeting = meetingService.getById(recognizeDTO.getMeetingId());
        if (meeting == null) {
            throw new BizException("会议号不存在");
        }
        if (meeting.getSpeechText() == null) meeting.setSpeechText(recognizeDTO.getContent());
        else meeting.setSpeechText(meeting.getSpeechText() + "\n" + recognizeDTO.getContent());
        meetingService.updateById(meeting);
    }

}
