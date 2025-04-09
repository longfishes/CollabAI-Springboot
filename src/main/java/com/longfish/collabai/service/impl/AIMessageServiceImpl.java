package com.longfish.collabai.service.impl;

import com.longfish.collabai.constant.RabbitMQConstant;
import com.longfish.collabai.context.AIStrategyContext;
import com.longfish.collabai.context.BaseContext;
import com.longfish.collabai.enums.StatusCodeEnum;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.pojo.dto.ChatDTO;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.pojo.vo.ChatVO;
import com.longfish.collabai.properties.HengProperties;
import com.longfish.collabai.service.AIMessageService;
import com.longfish.collabai.service.IMeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class AIMessageServiceImpl implements AIMessageService {

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private HengProperties hengProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AIStrategyContext aiStrategyContext;

    @Override
    public void summarizeMeeting(String meetingId) {
        if (meetingId == null) {
            throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);
        }
        Meeting meeting = meetingService.getById(meetingId);

        if (meeting == null) {
            throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);
        }

        if (!BaseContext.getCurrentId().equals(meeting.getHolderId())) {
            throw new BizException(StatusCodeEnum.FORBIDDEN);
        }

        rabbitTemplate.convertAndSend(
                RabbitMQConstant.SUMMARIZE_EXCHANGE,
                "*",
                new Message(meetingId.getBytes(), new MessageProperties())
        );
    }

    @Override
    public String syncAiMessage(String meetingId) {
        if (meetingId == null) {
            throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);
        }
        Meeting meeting = meetingService.getById(meetingId);

        if (meeting == null) {
            throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);
        }

        return meeting.getAiSummary();
    }

    @Override
    public String getAIToken() {
        try {
            String sign = getSign(hengProperties.getAppKey(), hengProperties.getAppSecret());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("appKey", hengProperties.getAppKey());
            headers.set("sign", sign);

            Map<String, String> body = new HashMap<>();
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    hengProperties.getBaseUrl() + "/open/api/xiaoheng/token",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.get("code").equals(0)) {
                return (String) responseBody.get("data");
            } else {
                throw new RuntimeException();
            }

        } catch (Exception e) {
            log.error("获取AI token失败", e);
            throw new BizException("获取token失败: " + e.getMessage());
        }
    }

    @Override
    public ChatVO chat(ChatDTO chatDTO) {
        return ChatVO.builder()
                .resp(aiStrategyContext.execChat(chatDTO.getContent()))
                .build();
    }

    public static String getSign(String key, String secret) {
        long timestamp = System.currentTimeMillis();
        String data = String.format("%d\n%s\n%s", timestamp, secret, key);
        Mac hmacSHA256 = null;
        try {
            hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSHA256.init(secretKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] sign = Objects.requireNonNull(hmacSHA256).doFinal(data.getBytes(StandardCharsets.UTF_8));
        return String.format("%d%s", timestamp,
                new String(new org.apache.commons.codec.binary.Base64().encode(sign), StandardCharsets.UTF_8));
    }
}
