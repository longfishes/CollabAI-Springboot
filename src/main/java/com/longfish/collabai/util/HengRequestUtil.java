package com.longfish.collabai.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longfish.collabai.properties.HengProperties;
import lombok.SneakyThrows;
import okhttp3.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.longfish.collabai.constant.CommonConstant.APPLICATION_JSON;

@Component
public class HengRequestUtil {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private HengProperties hengProperties;

    @SneakyThrows
    public String chat(String message) {
        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("role", "user");
        messageContent.put("content", message);

        List<Map<String, String>> messageList = new ArrayList<>();
        messageList.add(messageContent);

        return callHengApi(messageList);
    }

    @SneakyThrows
    public String summarySth(String message) {
        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("role", "user");
        messageContent.put("content", "帮我条理清晰地总结以下内容：\n" + message);

        List<Map<String, String>> messageList = new ArrayList<>();
        messageList.add(messageContent);

        return callHengApi(messageList);
    }

    @SneakyThrows
    public String callHengApi(List<Map<String, String>> history) {
        MediaType mediaType = MediaType.parse("application/json");

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("message", history);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(requestBodyMap);

        @SuppressWarnings("deprecation")
        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url(hengProperties.getBaseUrl() + "/open/api/v1/chat")
                .method("POST", body)
                .addHeader("appKey", hengProperties.getAppKey())
                .addHeader("sign", Objects.requireNonNull(getSign(hengProperties.getAppKey(),
                        hengProperties.getAppSecret())))
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
            Map jsonResponse = JSON.parseObject(responseString, Map.class);

            Map data = (Map) jsonResponse.get("data");
            if (data == null) {
                throw new RuntimeException("API返回数据为空");
            }

            Map messageData = (Map) data.get("message");
            if (messageData == null) {
                throw new RuntimeException("API返回消息为空");
            }

            String content = (String) messageData.get("content");
            if (content == null || content.trim().isEmpty()) {
                throw new RuntimeException("API返回内容为空");
            }

            return content;
        }
    }

    public static String getSign(String key, String secret) {
        try {
            long timestamp = System.currentTimeMillis();
            String data = String.format("%d\n%s\n%s", timestamp, secret, key);
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSHA256.init(secretKeySpec);
            byte[] sign = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return String.format("%d%s", timestamp, new String(Base64.encodeBase64(sign), StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
