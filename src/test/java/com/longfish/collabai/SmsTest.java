package com.longfish.collabai;

import com.alibaba.fastjson.JSON;
import com.aliyun.tea.TeaException;
import com.longfish.collabai.constant.RabbitMQConstant;
import com.longfish.collabai.pojo.dto.SmsDTO;
import com.longfish.collabai.properties.SmsProperties;
import com.longfish.collabai.util.PhoneUtil;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SmsTest {

    @Autowired
    private SmsProperties smsProperties;

    @Autowired
    private PhoneUtil phoneUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testMQ() {
        SmsDTO smsDTO = SmsDTO.builder()
                .phone("19065029907")
                .code("1919")
                .build();
        rabbitTemplate.convertAndSend(
                RabbitMQConstant.PHONE_EXCHANGE,
                "*",
                new Message(JSON.toJSONBytes(smsDTO), new MessageProperties())
        );
    }

    @Test
    public void testUtil() {
        phoneUtil.sendMessage(SmsDTO.
                builder()
                .phone("19065029907")
                .code("1145")
                .build()
        );
    }

    @Test
    void testSend() throws Exception {
        System.out.println(smsProperties);
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考。
        // 建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html。
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID。
                .setAccessKeyId(smsProperties.getAccessKeyId())
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
                .setAccessKeySecret(smsProperties.getAccessKeySecret());
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint = smsProperties.getEndpoint();
        com.aliyun.dysmsapi20170525.Client client = new com.aliyun.dysmsapi20170525.Client(config);
        com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setSignName(smsProperties.getSignName())
                .setTemplateCode(smsProperties.getTemplateCode())
                .setPhoneNumbers("19065029907")
                .setTemplateParam("{\"code\":\"114514\"}");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.sendSmsWithOptions(sendSmsRequest, runtime);
        } catch (TeaException error) {
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
    }
}
