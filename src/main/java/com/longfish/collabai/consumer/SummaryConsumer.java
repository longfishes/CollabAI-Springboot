package com.longfish.collabai.consumer;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.longfish.collabai.pojo.dto.AIMeetingSumDTO;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.service.IMeetingService;
import com.longfish.collabai.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.longfish.collabai.constant.RabbitMQConstant.AI_SUMMARIZE_QUEUE;

@Slf4j
@Component
@RabbitListener(queues = AI_SUMMARIZE_QUEUE)
public class SummaryConsumer {

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private RequestUtil requestUtil;

    @RabbitHandler
    public void process(byte[] data) {
        AIMeetingSumDTO sumDTO = JSON.parseObject(new String(data), AIMeetingSumDTO.class);
        log.info("开始ai总结会议：{}", sumDTO.getId());

        String content = "会议文档：" + sumDTO.getMdContent() + "会议录音详细记录：" + sumDTO.getSpeechText();
        String summarizeRes = requestUtil.summarySth(content);
        sumDTO.setAiSummary(summarizeRes);

        Meeting meeting = BeanUtil.copyProperties(sumDTO, Meeting.class);

        meetingService.updateById(meeting);
    }
}
