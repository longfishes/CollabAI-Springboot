package com.longfish.collabai.task;

import com.alibaba.fastjson.JSON;
import com.longfish.collabai.socket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class KeepAliveTask {

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * cron: 每 5 秒
     * desc: 心跳包
     */
    @Scheduled(cron = "*/5 * * * * *")
    public void heartBeat() {
        Map<String, String> map = new HashMap<>();
        map.put("data", null);
        map.put("userId", "-1");
        map.put("nickName", "system");

        webSocketServer.broadcasts(JSON.toJSONString(map));
    }


}
