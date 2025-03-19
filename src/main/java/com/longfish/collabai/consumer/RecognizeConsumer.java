package com.longfish.collabai.consumer;

import com.alibaba.fastjson.JSON;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.pojo.dto.RecognizeDTO;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.service.IMeetingService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.longfish.collabai.constant.RabbitMQConstant.RECOGNIZE_QUEUE;

@Slf4j
@Component
@RabbitListener(queues = RECOGNIZE_QUEUE)
public class RecognizeConsumer {

    @Autowired
    private IMeetingService meetingService;
    private String lastSpeechText = "";

    @RabbitHandler
    public void process(byte[] data) {
        RecognizeDTO recognizeDTO = JSON.parseObject(new String(data), RecognizeDTO.class);
        Meeting meeting = meetingService.getById(recognizeDTO.getMeetingId());
        if (meeting == null) {
            throw new BizException("会议号不存在");
        }
        String[] lastSpeechTextSplit = lastSpeechText.split(":");
        if (!lastSpeechText.isEmpty() && lastSpeechTextSplit[1].length() >= recognizeDTO.getContent().split(":")[1].length()) {
            String addText = getAddText(lastSpeechTextSplit, meeting);
            meeting.setSpeechText(addText);
            meetingService.updateById(meeting);
        }
        lastSpeechText = recognizeDTO.getContent();
    }

    @NotNull
    private String getAddText(String[] lastSpeechTextSplit, Meeting meeting) {
        String addText = "";
        if (lastSpeechTextSplit[1].length() > 1) {
            char secondChar = lastSpeechTextSplit[1].charAt(1);
            String secondCharStr = String.valueOf(secondChar);
            String regex = "[\\p{IsPunctuation}！？。；：“”‘’（）【】—…《》]";
            if (secondCharStr.matches(regex)) {
                addText = meeting.getSpeechText() + secondChar + "\n" + lastSpeechTextSplit[0] + ":" + lastSpeechTextSplit[1].replaceAll(regex, "");
            } else addText = meeting.getSpeechText() + "\n" + lastSpeechText;

        }
        return addText;
    }


}
