package com.longfish.collabai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.longfish.collabai.properties.AIProperties;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.longfish.collabai.util.RequestUtil.getSign;

@SpringBootTest
class CollabAISpringbootApplicationTests {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private AIProperties aiProperties;

    @Test
    public void testAiReq() throws IOException {
        MediaType mediaType = MediaType.parse("application/json");

        // 创建消息内容
        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("role", "user");
        messageContent.put("content", "帮我总结一下这份文档");

        ArrayList<Map<String, String>> messageList = new ArrayList<>();
        messageList.add(messageContent);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("message", messageList);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(requestBodyMap);

        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url(aiProperties.getBaseUrl() + "/open/api/v1/chat")
                .method("POST", body)
                .addHeader("appKey", aiProperties.getAppKey())
                .addHeader("sign", Objects.requireNonNull(getSign(aiProperties.getAppKey(),
                        aiProperties.getAppSecret())))
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = okHttpClient.newCall(request).execute();
        System.out.println(Objects.requireNonNull(response.body()).string());
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
