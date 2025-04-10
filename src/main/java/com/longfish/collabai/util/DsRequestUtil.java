package com.longfish.collabai.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longfish.collabai.properties.DeepSeekProperties;
import jakarta.websocket.Session;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.longfish.collabai.constant.CommonConstant.APPLICATION_JSON;
import static com.longfish.collabai.constant.CommonConstant.FINISH_TAG;

@Component
@Slf4j
public class DsRequestUtil {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private DeepSeekProperties deepSeekProperties;

    @SneakyThrows
    public String chatStream(Session session, List<Map<String, String>> history) {
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
                            session.getBasicRemote().sendText(content);
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
            session.getBasicRemote().sendText(FINISH_TAG);
            return fullContent.toString();
        }
    }

    @SneakyThrows
    public String chat(String message) {
        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("role", "user");
        messageContent.put("content", message);

        List<Map<String, String>> messageList = new ArrayList<>();
        messageList.add(messageContent);

        return callDeepSeekApi(messageList);
    }

    @SneakyThrows
    public String summarizeSth(String message) {
        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("role", "user");
        messageContent.put("content", "帮我条理清晰地总结以下内容：\n" + message);

        List<Map<String, String>> messageList = new ArrayList<>();
        messageList.add(messageContent);

        return callDeepSeekApi(messageList);
    }

    @SneakyThrows
    public String callDeepSeekApi(List<Map<String, String>> history) {
        MediaType mediaType = MediaType.parse("application/json");

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("messages", history);
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
