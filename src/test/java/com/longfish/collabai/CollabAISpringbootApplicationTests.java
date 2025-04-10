package com.longfish.collabai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longfish.collabai.context.AIStrategyContext;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.properties.DeepSeekProperties;
import com.longfish.collabai.properties.HengProperties;
import com.longfish.collabai.service.IMeetingService;
import com.longfish.collabai.util.HengRequestUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.longfish.collabai.constant.CommonConstant.APPLICATION_JSON;
import static com.longfish.collabai.util.HengRequestUtil.getSign;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class CollabAISpringbootApplicationTests {

    @Autowired
    private HengRequestUtil hengRequestUtil;

    @Autowired
    private HengProperties hengProperties;

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private AIStrategyContext aiStrategyContext;

    @Autowired
    private DeepSeekProperties deepSeekProperties;

    @Autowired
    private OkHttpClient okHttpClient;

    @SneakyThrows
    @Test
    public void testStreamReq() {
        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("role", "user");
        messageContent.put("content", "自我介绍一下");

        List<Map<String, String>> history = new ArrayList<>();
        history.add(messageContent);
        MediaType mediaType = MediaType.parse("application/json");

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("messages", history);
        requestBodyMap.put("model", "deepseek-chat");
        requestBodyMap.put("temperature", 0.7);
        requestBodyMap.put("max_tokens", 2000);
        requestBodyMap.put("stream", true);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(requestBodyMap);

        @SuppressWarnings("deprecation")
        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url(deepSeekProperties.getBaseUrl() + "/chat/completions")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + deepSeekProperties.getApiKey())
                .addHeader("User-Agent", "collabai-backend")
                .addHeader("Content-Type", APPLICATION_JSON)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("API调用失败: " + response.code() + " " + response.message());
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new RuntimeException("API返回为空");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody.byteStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder fullContent = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                if (!line.startsWith("data: ")) continue;

                String jsonData = line.substring(6); // 去掉 "data: " 前缀
                if ("[DONE]".equals(jsonData)) {
                    log.info("Stream finished");
                    break;
                }

                JSONObject jsonResponse = JSON.parseObject(jsonData);
                List<JSONObject> choices = jsonResponse.getJSONArray("choices").toJavaList(JSONObject.class);

                if (choices != null && !choices.isEmpty()) {
                    JSONObject delta = choices.get(0).getJSONObject("delta");
                    if (delta != null) {
                        String content = delta.getString("content");
                        if (content != null) {
                            System.out.print(content);
                            fullContent.append(content);
                        }
                    }
                }

                // 打印 usage 信息（如果存在）
                JSONObject usage = jsonResponse.getJSONObject("usage");
                if (usage != null && !usage.isEmpty()) {
                    log.debug("Usage info: " + usage);
                }
            }

            log.debug("Full content: " + fullContent);
        }
    }

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
