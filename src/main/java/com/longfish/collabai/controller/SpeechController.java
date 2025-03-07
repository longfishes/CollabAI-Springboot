package com.longfish.collabai.controller;

import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.pojo.Result;
import com.longfish.collabai.ttl.WebSocketManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@RestController
@RequestMapping("/speech")
@Slf4j
@Tag(name = "语音识别")
public class SpeechController {

    @Autowired
    private WebSocketManager webSocketManager;

    @PostMapping("/recognize")
    public Result<?> recognizeSpeech(@RequestBody byte[] audioData) {
        try {
            // 验证音频数据
            if (audioData.length % 2 != 0) {
                return Result.error("无效的PCM数据：数据长度必须是2的倍数");
            }

            // 打印音频数据的基本信息
            log.debug("接收到音频数据: {} 字节, {} 个采样点",
                     audioData.length,
                     audioData.length / 2);

            // 验证采样值范围
            ByteBuffer buffer = ByteBuffer.wrap(audioData).order(ByteOrder.LITTLE_ENDIAN);
            int sampleCount = 0;

            while (buffer.hasRemaining() && sampleCount < 10) {  // 只检查前10个样本
                sampleCount++;
            }

            boolean success = webSocketManager.sendAudioData(audioData);
            if (success) {
                return Result.success("音频数据已接收");
            } else {
                throw new BizException("语音服务连接失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("处理音频数据失败", e);
            throw new BizException("处理音频数据失败：" + e.getMessage());
        }
    }
}
