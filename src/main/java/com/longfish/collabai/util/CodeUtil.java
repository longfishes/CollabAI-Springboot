package com.longfish.collabai.util;

import com.longfish.collabai.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

import static com.longfish.collabai.constant.CommonConstant.CODE;

@Component
public class CodeUtil {

    @Autowired
    private RedisService redisService;

    public String getRandomCode() {
        Random seed = new Random();
        StringBuilder s = new StringBuilder(String.valueOf(seed.nextInt(1000000)));
        if (s.length() < 7) {
            int len = 6 - s.length();
            for (int i = 0; i < len; i++) {
                s.insert(0, '0');
            }
        }
        return s.toString();
    }

    public void insert(String username, String code) {
        redisService.set(CODE + "::" + username, code, 15 * 60);
    }

    public String get(String username) {
        Object code = redisService.get(CODE + "::" + username);
        if (code != null) return (String) code;
        return null;
    }
}
