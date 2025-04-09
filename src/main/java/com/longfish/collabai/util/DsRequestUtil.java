package com.longfish.collabai.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longfish.collabai.properties.DeepSeekProperties;
import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.longfish.collabai.constant.CommonConstant.APPLICATION_JSON;

@Component
public class DsRequestUtil {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private DeepSeekProperties deepSeekProperties;

    @SneakyThrows
    public String chat(String message) {
        return callDeepSeekApi(message);
    }

    @SneakyThrows
    public String summarizeSth(String message) {
        return callDeepSeekApi("帮我条理清晰地总结以下内容：\n" + message);
    }

    @SneakyThrows
    private String callDeepSeekApi(String message) {
        MediaType mediaType = MediaType.parse("application/json");

        // 创建消息内容
        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("role", "user");
        messageContent.put("content", message);

        List<Map<String, String>> messageList = new ArrayList<>();
        messageList.add(messageContent);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("messages", messageList);
        requestBodyMap.put("model", "deepseek-chat");
        requestBodyMap.put("temperature", 0.7);
        requestBodyMap.put("max_tokens", 2000);

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

            String responseString = responseBody.string();
            JSONObject jsonResponse = JSON.parseObject(responseString);

            List<JSONObject> choices = jsonResponse.getJSONArray("choices").toJavaList(JSONObject.class);
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("API返回结果为空");
            }

            JSONObject msg = choices.get(0).getJSONObject("message");
            if (msg == null) {
                throw new RuntimeException("API返回消息为空");
            }

            String content = msg.getString("content");
            if (content == null || content.trim().isEmpty()) {
                throw new RuntimeException("API返回内容为空");
            }

            return content;
        }
    }
}
