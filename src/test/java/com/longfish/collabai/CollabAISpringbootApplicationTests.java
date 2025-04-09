package com.longfish.collabai;

import com.longfish.collabai.context.AIStrategyContext;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.properties.HengProperties;
import com.longfish.collabai.service.IMeetingService;
import com.longfish.collabai.util.HengRequestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.longfish.collabai.util.HengRequestUtil.getSign;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CollabAISpringbootApplicationTests {

    @Autowired
    private HengRequestUtil hengRequestUtil;

    @Autowired
    private HengProperties hengProperties;

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private AIStrategyContext aiStrategyContext;

    @Test
    public void testAIStrategy() {
        String summarizeRes = aiStrategyContext.execSummarizeSth("你好");
        System.out.println(summarizeRes);
    }

    @Test
    public void testQueryStartMeeting() {
        LocalDateTime now = LocalDateTime.now();

        // 查询所有正在进行的会议
        List<Meeting> ongoingMeetings = meetingService.lambdaQuery()
                .le(Meeting::getStartTime, now)
                .ge(Meeting::getEndTime, now)
                .list();

        ongoingMeetings.forEach(meeting -> System.out.println("正在进行的会议: " + meeting.getTitle() +
                ", 开始时间: " + meeting.getStartTime() + ", 结束时间: " + meeting.getEndTime()));
    }

    @Test
    public void testAiReq() {
        String md = """
                # AI 交流会议

                ## 会议议程
                1. 欢迎致辞
                2. AI 技术现状
                3. 未来发展趋势
                4. 讨论与问答

                ## 会议内容

                ### 欢迎致辞
                大家好，欢迎参加今天的 AI 交流会议。我们将讨论 AI 技术的现状和未来发展趋势。

                ### AI 技术现状
                - 当前 AI 技术在图像识别、自然语言处理等领域取得了显著进展。
                - 深度学习和神经网络是推动 AI 发展的关键技术。

                ### 未来发展趋势
                - AI 将在自动驾驶、医疗诊断等领域发挥更大作用。
                - 伦理和隐私问题将成为 AI 发展的重要议题。

                ### 讨论与问答
                - 与会者就 AI 在教育领域的应用展开了热烈讨论。
                - 提出了一些关于 AI 伦理的疑问，并进行了深入探讨。

                ## 会议总结
                本次会议深入探讨了 AI 技术的现状和未来发展趋势，提出了许多有价值的观点和建议。""";
        String rec = """
                主持人：大家好，欢迎参加今天的 AI 交流会议。我们将讨论 AI 技术的现状和未来发展趋势。
                                
                发言人A：当前 AI 技术在图像识别、自然语言处理等领域取得了显著进展。深度学习和神经网络是推动 AI 发展的关键技术。
                                
                发言人B：未来，AI 将在自动驾驶、医疗诊断等领域发挥更大作用。同时，伦理和隐私问题将成为 AI 发展的重要议题。
                                
                主持人：现在进入讨论与问答环节。请大家踊跃发言。
                                
                与会者C：我认为 AI 在教育领域的应用潜力巨大，但我们需要考虑如何保护学生的隐私。
                                
                与会者D：关于 AI 伦理，我有一个问题，如何确保 AI 系统的公平性？
                                
                主持人：感谢大家的参与和讨论。今天的会议到此结束。""";
        String content = "会议文档：" + md + "会议录音详细记录：" + rec;
        String res = hengRequestUtil.summarySth(content);
        System.out.println(res);
    }

    @Test
    public void testGetSign() {
        String sign = getSign(hengProperties.getAppKey() , hengProperties.getAppSecret());
        System.out.println(sign);
    }

    @Test
    void loadContext() {
    }

}
