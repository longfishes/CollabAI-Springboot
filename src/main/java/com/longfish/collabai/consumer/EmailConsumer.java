package com.longfish.collabai.consumer;

import com.alibaba.fastjson.JSON;
import com.longfish.collabai.pojo.dto.EmailDTO;
import com.longfish.collabai.util.CodeUtil;
import com.longfish.collabai.util.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.longfish.collabai.constant.RabbitMQConstant.EMAIL_QUEUE;

@Slf4j
@Component
@RabbitListener(queues = EMAIL_QUEUE)
public class EmailConsumer {

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private CodeUtil codeUtil;

    @RabbitHandler
    public void process(byte[] data) {
        EmailDTO emailDTO = JSON.parseObject(new String(data), EmailDTO.class);
        log.info("开始发送邮箱验证码TO：{}", emailDTO.getEmail());
        emailUtil.sendHtmlMail(emailDTO);
        codeUtil.insert(emailDTO.getEmail(), String.valueOf(emailDTO.getCode()));
    }

}
