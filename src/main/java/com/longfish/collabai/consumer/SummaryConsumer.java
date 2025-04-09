package com.longfish.collabai.consumer;

import com.longfish.collabai.context.AIStrategyContext;
import com.longfish.collabai.enums.StatusCodeEnum;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.service.IMeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.longfish.collabai.constant.RabbitMQConstant.SUMMARIZE_QUEUE;

@Slf4j
@Component
@RabbitListener(queues = SUMMARIZE_QUEUE)
public class SummaryConsumer {

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private AIStrategyContext aiStrategyContext;

    @RabbitHandler
    public void process(byte[] data) {
        String meetingId = new String(data);
        log.info("开始ai总结会议：{}", meetingId);

        Meeting meeting = meetingService.getById(meetingId);
        if (meeting == null) throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);

        String content =  "会议主题：" + meeting.getTitle() +
                "\n会议文档：" + meeting.getMdContent() +
                "\n会议录音详细记录：" + meeting.getSpeechText();
        String summarizeRes = aiStrategyContext.execSummarizeSth(content);

        meeting.setAiSummary(summarizeRes);
        meetingService.updateById(meeting);
    }
}
