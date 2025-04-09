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
    public String summarySth(String message) {
        MediaType mediaType = MediaType.parse("application/json");

        // 创建消息内容
        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("role", "user");
        messageContent.put("content", "帮我条理清晰地总结以下内容：\n" + message);

        List<Map<String, String>> messageList = new ArrayList<>();
        messageList.add(messageContent);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("message", messageList);

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
        Response response = okHttpClient.newCall(request).execute();
        return (String) (((Map) (((Map) (JSON.parseObject(Objects.requireNonNull(response.body()).string(), Map.class)
                .get("data"))).get("message"))).get("content"));
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
