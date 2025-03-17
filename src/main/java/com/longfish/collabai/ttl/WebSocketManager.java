package com.longfish.collabai.ttl;

import com.longfish.collabai.properties.TtlProperties;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class WebSocketManager {

    private final RTASRApp rtasrApp;
    private RTASRApp.MyWebSocketClient client;
    private final Object lock = new Object();
    private volatile boolean isConnected = false;
    private static final int TIMEOUT_SECONDS = 15;
    private volatile long lastSendTime;

    @Autowired
    private TtlProperties ttlProperties;

    @Autowired
    public WebSocketManager(RTASRApp rtasrApp) {
        this.rtasrApp = rtasrApp;
    }

    public void setMeetingId(String meetingId) {
        rtasrApp.setMeetingId(meetingId);
    }

    public void setCurrentName(String currentName) {
        rtasrApp.setCurrentName(currentName);
    }

    @Scheduled(fixedRate = 1000)
    public void checkConnection() {
        if (isConnected && System.currentTimeMillis() - lastSendTime > TIMEOUT_SECONDS * 1000) {
            synchronized (lock) {
                if (client != null) {
                    log.info("连接超时，关闭连接");
                    client.close();
                    client = null;
                    isConnected = false;
                }
            }
        }
    }

    private boolean initConnection() {
        synchronized (lock) {
            if (isConnected && client != null && client.getReadyState() == WebSocket.READYSTATE.OPEN) {
                return true;
            }

            try {
                if (client != null) {
                    client.close();
                    client = null;
                }

                URI url = new URI(ttlProperties.getWsBaseUrl() +
                        rtasrApp.getHandShakeParams(ttlProperties.getAppId(), ttlProperties.getSecretKey()));
                DraftWithOrigin draft = new DraftWithOrigin(ttlProperties.getHttpBaseUrl());
                CountDownLatch handshakeSuccess = new CountDownLatch(1);
                CountDownLatch connectClose = new CountDownLatch(1);

                client = rtasrApp.new MyWebSocketClient(url, draft, handshakeSuccess, connectClose);
                client.connect();

                int attempts = 0;
                while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN) && attempts < 5) {
                    log.info("正在建立连接...");
                    //noinspection BusyWait
                    Thread.sleep(1000);
                    attempts++;
                }

                if (client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
                    if (handshakeSuccess.await(5, TimeUnit.SECONDS)) {
                        isConnected = true;
                        lastSendTime = System.currentTimeMillis();
                        log.info("连接建立成功");
                        return true;
                    }
                }

                log.error("连接建立失败");
                return false;
            } catch (Exception e) {
                log.error("建立连接时发生错误", e);
                return false;
            }
        }
    }

    public boolean sendAudioData(byte[] audioData) {
        synchronized (lock) {
            if (!isConnected || client == null || client.getReadyState() != WebSocket.READYSTATE.OPEN) {
                if (!initConnection()) {
                    return false;
                }
            }

            try {
                rtasrApp.send(client, audioData);
                lastSendTime = System.currentTimeMillis();
                return true;
            } catch (Exception e) {
                log.error("发送音频数据失败", e);
                isConnected = false;
                return false;
            }
        }
    }

    @PreDestroy
    public void cleanup() {
        synchronized (lock) {
            if (client != null) {
                client.close();
                client = null;
            }
            isConnected = false;
        }
    }
}
