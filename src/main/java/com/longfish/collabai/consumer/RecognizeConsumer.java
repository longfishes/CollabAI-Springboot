package com.longfish.collabai.consumer;

import com.alibaba.fastjson.JSON;
import com.longfish.collabai.pojo.dto.RecognizeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.longfish.collabai.constant.RabbitMQConstant.RECOGNIZE_QUEUE;

@Slf4j
@Component
@RabbitListener(queues = RECOGNIZE_QUEUE)
public class RecognizeConsumer {

    @RabbitHandler
    public void process(byte[] data) {
        RecognizeDTO recognizeDTO = JSON.parseObject(new String(data), RecognizeDTO.class);
        log.debug(recognizeDTO.getContent());
    }

}
