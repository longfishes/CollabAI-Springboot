package com.longfish.collabai.ttl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.longfish.collabai.constant.RabbitMQConstant;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.pojo.dto.RecognizeDTO;
import com.longfish.collabai.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import static com.longfish.collabai.constant.DatabaseConstant.REDIS_KEY_MEETING_ID;

/**
 * 实时转写调用
 *
 * @author longfish
 */
@Component
@Slf4j
public class RTASRApp {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisService redisService;

    // 生成握手参数
    public String getHandShakeParams(String appId, String secretKey) {
        String ts = System.currentTimeMillis() / 1000 + "";
        String signa;
        try {
            signa = EncryptUtil.HmacSHA1Encrypt(
                    Objects.requireNonNull(EncryptUtil.MD5(appId + ts)), secretKey);
            return "?appid=" + appId + "&ts=" + ts + "&signa=" +
                    URLEncoder.encode(signa, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BizException("RTASRApp : 生成握手参数失败");
        }
    }

    public void send(WebSocketClient client, byte[] bytes) {
        if (client.isClosed()) {
            throw new BizException("RTASRApp : client connect closed!");
        }
        client.send(bytes);
    }

    public class MyWebSocketClient extends WebSocketClient {

        private final CountDownLatch handshakeSuccess;
        private final CountDownLatch connectClose;

        public MyWebSocketClient(URI serverUri, Draft protocolDraft, CountDownLatch handshakeSuccess, CountDownLatch connectClose) {
            super(serverUri, protocolDraft);
            this.handshakeSuccess = handshakeSuccess;
            this.connectClose = connectClose;
            if (serverUri.toString().contains("wss")) {
                trustAllHosts(this);
            }
        }

        @Override
        public void onOpen(ServerHandshake handshake) {
            log.info("连接建立成功！");
        }

        @Override
        public void onMessage(String msg) {
            JSONObject msgObj = JSON.parseObject(msg);
            String action = msgObj.getString("action");
            if (Objects.equals("started", action)) {
                // 握手成功
                log.info("握手成功！sid: " + msgObj.getString("sid"));
                handshakeSuccess.countDown();
            } else if (Objects.equals("result", action)) {
                // 转写结果
                String msgObjString = msgObj.getString("data");
                String ls = JSON.parseObject(msgObjString).getString("ls");

                log.info(getContent(msgObjString) + "\tls: " + ls);

            } else if (Objects.equals("error", action)) {
                // 连接发生错误
                log.error("Error: " + msg);
            }
        }

        @Override
        public void onError(Exception e) {
            e.printStackTrace();
            log.error("RTASRApp : 连接发生错误 : " + e.getMessage());
        }

        @Override
        public void onClose(int arg0, String arg1, boolean arg2) {
            log.info("连接关闭");
            connectClose.countDown();
        }

        @Override
        public void onMessage(ByteBuffer bytes) {
            log.info("服务端返回：" +
                    new String(bytes.array(), StandardCharsets.UTF_8));
        }

        public void trustAllHosts(MyWebSocketClient appClient) {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
                }
            }};

            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                appClient.setSocket(sc.getSocketFactory().createSocket());
            } catch (Exception e) {
                throw new BizException("RTASRApp trustAllHosts : " + e.getMessage());
            }
        }
    }

    // 把转写结果解析为句子
    public String getContent(String message) {
        StringBuilder resultBuilder = new StringBuilder();
        String rl = "";
        try {
            JSONObject messageObj = JSON.parseObject(message);
            JSONObject cn = messageObj.getJSONObject("cn");

            JSONObject st = cn.getJSONObject("st");
            JSONArray rtArr = st.getJSONArray("rt");
            for (int i = 0; i < rtArr.size(); i++) {
                JSONObject rtArrObj = rtArr.getJSONObject(i);
                JSONArray wsArr = rtArrObj.getJSONArray("ws");
                for (int j = 0; j < wsArr.size(); j++) {
                    JSONObject wsArrObj = wsArr.getJSONObject(j);
                    JSONArray cwArr = wsArrObj.getJSONArray("cw");
                    for (int k = 0; k < cwArr.size(); k++) {
                        JSONObject cwArrObj = cwArr.getJSONObject(k);
                        String wStr = cwArrObj.getString("w");
                        rl = cwArrObj.getString("rl");
                        resultBuilder.append(wStr);
                    }
                }
            }
        } catch (Exception e) {
            return message;
        }

        String result = resultBuilder.toString();
//        String currentName = BaseContext.getCurrentName();
        String currentName = "test";
        String meetingId = (String) redisService.get(REDIS_KEY_MEETING_ID);

        RecognizeDTO recognizeDTO = RecognizeDTO.builder()
                .content(currentName + ": " + result)
                .meetingId(meetingId)
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConstant.RECOGNIZE_EXCHANGE,
                "*",
                new Message(JSON.toJSONBytes(recognizeDTO), new MessageProperties())
        );
        return "rl: " + rl + "\tresult: " + result;
    }
}
