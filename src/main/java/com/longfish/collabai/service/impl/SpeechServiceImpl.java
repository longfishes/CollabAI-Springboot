package com.longfish.collabai.service.impl;

import com.longfish.collabai.context.BaseContext;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.service.IMeetingService;
import com.longfish.collabai.service.SpeechService;
import com.longfish.collabai.ttl.WebSocketManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Service
@Slf4j
public class SpeechServiceImpl implements SpeechService {

    @Autowired
    private WebSocketManager webSocketManager;

    @Autowired
    private IMeetingService meetingService;

    @Override
    public void recognizeSpeech(byte[] audioData, String meetingId) {
        try {
            if (audioData.length % 2 != 0) {
                throw new BizException("无效的PCM数据：数据长度必须是2的倍数");
            }

            log.debug("接收到音频数据: {} 字节, {} 个采样点",
                    audioData.length,
                    audioData.length / 2);

            ByteBuffer buffer = ByteBuffer.wrap(audioData).order(ByteOrder.LITTLE_ENDIAN);
            int sampleCount = 0;

            while (buffer.hasRemaining() && sampleCount < 10) {
                sampleCount++;
            }

            boolean success = webSocketManager.sendAudioData(audioData);
            if (!success) {
                throw new BizException("语音服务连接失败，请稍后重试");
            }

        } catch (Exception e) {
            log.error("处理音频数据失败", e);
            throw new BizException("处理音频数据失败：" + e.getMessage());
        }

        webSocketManager.setMeetingId(meetingId);
        webSocketManager.setCurrentName(BaseContext.getCurrentName());
    }

    @Override
    public String syncSpeechText(String meetingId) {
        return meetingService.getById(meetingId).getSpeechText();
    }
}
