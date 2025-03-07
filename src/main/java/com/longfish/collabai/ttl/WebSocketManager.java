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
    private RTASRApp.MyWebSocketClient client;
    private final Object lock = new Object();
    private volatile boolean isConnected = false;
    private static final int TIMEOUT_SECONDS = 15;
    private volatile long lastSendTime;

    @Autowired
    private TtlProperties ttlProperties;

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

                URI url = new URI(RTASRApp.BASE_URL +
                        RTASRApp.getHandShakeParams(ttlProperties.getAppId(), ttlProperties.getSecretKey()));
                DraftWithOrigin draft = new DraftWithOrigin(RTASRApp.ORIGIN);
                CountDownLatch handshakeSuccess = new CountDownLatch(1);
                CountDownLatch connectClose = new CountDownLatch(1);

                client = new RTASRApp.MyWebSocketClient(url, draft, handshakeSuccess, connectClose);
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
                RTASRApp.send(client, audioData);
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
