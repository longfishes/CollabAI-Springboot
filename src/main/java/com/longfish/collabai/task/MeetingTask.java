package com.longfish.collabai.task;

import com.longfish.collabai.constant.RabbitMQConstant;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.service.IMeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class MeetingTask {

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 正在进行的会议 ai总结生成
     * 每 10分钟 检测一次
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void autoSummarize() {
        LocalDateTime now = LocalDateTime.now();

        List<Meeting> ongoingMeetings = meetingService.lambdaQuery()
                .le(Meeting::getStartTime, now)
                .ge(Meeting::getEndTime, now)
                .list();

        ongoingMeetings.forEach(meeting -> rabbitTemplate.convertAndSend(
                RabbitMQConstant.SUMMARIZE_EXCHANGE,
                "*",
                new Message(meeting.getId().getBytes(), new MessageProperties())
        ));
    }
}
