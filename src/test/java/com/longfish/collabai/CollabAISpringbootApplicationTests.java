package com.longfish.collabai;

import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.properties.AIProperties;
import com.longfish.collabai.service.IMeetingService;
import com.longfish.collabai.util.RequestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.longfish.collabai.util.RequestUtil.getSign;

@SpringBootTest
class CollabAISpringbootApplicationTests {

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private AIProperties aiProperties;

    @Autowired
    private IMeetingService meetingService;

    @Test
    public void testQueryStartMeeting() {
        LocalDateTime now = LocalDateTime.now();

        // 查询所有正在进行的会议
        List<Meeting> ongoingMeetings = meetingService.lambdaQuery()
                .le(Meeting::getStartTime, now)
                .ge(Meeting::getEndTime, now)
                .list();

        ongoingMeetings.forEach(meeting -> {
            System.out.println("正在进行的会议: " + meeting.getTitle() +
                    ", 开始时间: " + meeting.getStartTime() + ", 结束时间: " + meeting.getEndTime());
        });
    }

    @Test
    public void testAiReq() {
        String res = requestUtil.summarySth("content");
        System.out.println(res);
    }

    @Test
    public void testGetSign() {
        String sign = getSign(aiProperties.getAppKey() , aiProperties.getAppSecret());
        System.out.println(sign);
    }

    @Test
    void loadContext() {
    }

}
