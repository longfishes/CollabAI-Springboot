package com.longfish.collabai.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SystemTask {

    @Scheduled(cron = "0 0/10 * * * ?")
    public void heartBeat() {
    }
}
