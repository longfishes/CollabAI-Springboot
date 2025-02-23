package com.longfish.collabai.util;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.pojo.dto.SmsDTO;
import com.longfish.collabai.properties.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class PhoneUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    public static boolean isValid(String phone) {
        if (phone == null) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    @Autowired
    private SmsProperties smsProperties;

    public void sendMessage(SmsDTO smsDTO) {
        Config config = new Config()
                .setAccessKeyId(smsProperties.getAccessKeyId())
                .setAccessKeySecret(smsProperties.getAccessKeySecret());
        config.endpoint = smsProperties.getEndpoint();
        com.aliyun.dysmsapi20170525.Client client;
        try {
            client = new Client(config);
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setSignName(smsProperties.getSignName())
                    .setTemplateCode(smsProperties.getTemplateCode())
                    .setPhoneNumbers(smsDTO.getPhone())
                    .setTemplateParam("{\"code\":\"" + smsDTO.getCode() + "\"}");
            RuntimeOptions runtime = new RuntimeOptions();
            client.sendSmsWithOptions(sendSmsRequest, runtime);
        } catch (Exception e) {
            throw new BizException("验证码发送失败");
        }
    }
}
