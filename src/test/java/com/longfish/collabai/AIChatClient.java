//package com.longfish.collabai;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import org.apache.commons.codec.binary.Base64;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.net.URI;
//import java.nio.charset.StandardCharsets;
//import java.util.Scanner;
//
//public class AIChatClient {
//
//    private static final String appKey = "";
//    private static final String appSecret = "";
//    private static final String WS_BASE_URL = "wss://www.das-ai.com/open/ws/v1/chat";  // ws聊天
//    private static final String HTTP_BASE_URL = "https://www.das-ai.com/open/api/v1/chat";  // http聊天
//
//    private static boolean isWaitingForAIResponse = false;  // 控制用户输入状态
//    private static final JSONArray messages = new JSONArray();
//
//    // 获取签名
//    public static String getSign(String key, String secret) {
//        try {
//            long timestamp = System.currentTimeMillis();
//            String data = String.format("%d\n%s\n%s", timestamp, secret, key);
//            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
//            hmacSHA256.init(secretKeySpec);
//            byte[] sign = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
//            return String.format("%d%s", timestamp, new String(Base64.encodeBase64(sign), StandardCharsets.UTF_8));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    // 构建WebSocket的URI
//    public static URI buildWebSocketURI() {
//        String sign = getSign(appKey, appSecret);
//        return URI.create(WS_BASE_URL + "?appKey=" + appKey + "&sign=" + sign);
//    }
//
//    public static void main(String[] args) throws Exception {
//
//        // 获取 WebSocket 连接 URI
//        URI uri = buildWebSocketURI();
//
//        WebSocketClient client = new WebSocketClient(uri) {
//
//            @Override
//            public void onOpen(ServerHandshake serverHandshake) {
//                System.out.println("WebSocket connection established with server!");
//            }
//
//            @Override
//            public void onMessage(String message) {
//                try {
//                    // 解析响应内容
//                    JSONObject response = JSONObject.parseObject(message);  // 使用 FastJSON 解析响应
////                    System.out.println(response);
//                    int status = (int) response.get("status");
//
//                    // 获取 message 字段
//                    Object messageObj = response.get("message");
//                    if (status == 0) {
//                        isWaitingForAIResponse = false;
//
//                        // 创建消息
//                        JSONObject msg = new JSONObject();
//                        msg.put("role", "user");
//                        msg.put("content", ((JSONObject) messageObj).getString("content"));
//
//                        messages.add(msg);
//                        System.out.println();
//                        return;
//
//                    } else if (status != 1) {
//                        isWaitingForAIResponse = false;
//                        System.out.println(response);
//                        return;
//                    }
//
//                    // 如果 message 是 JSONObject 类型
//                    if (messageObj instanceof JSONObject) {
//                        JSONObject msg = (JSONObject) messageObj;
//                        if (msg.getString("role").equals("assistant")) {
//                            String content = msg.getString("content");
////                            System.out.print("AI: ");
//
//                            // 实现打字机效果，逐字显示
//                            for (int i = 0; i < content.length(); i++) {
//                                System.out.print(content.charAt(i));
////                                Thread.sleep(100);  // 设置每个字符显示的时间
//                            }
////                            System.out.println();  // 换行
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//            @Override
//            public void onClose(int code, String reason, boolean remote) {
//                System.out.println("Closed: " + reason);
//            }
//
//            @Override
//            public void onError(Exception ex) {
//                ex.printStackTrace();
//            }
//        };
//
//        client.connectBlocking();
//
//        // 初始化 Scanner
//        Scanner scanner = new Scanner(System.in);
//
//        while (true) {
//
//            while (isWaitingForAIResponse) {
//                Thread.sleep(100);
//            }
//
//            System.out.print("You: ");
//            String userInput = scanner.nextLine();
//            isWaitingForAIResponse = true;
//            System.out.print("AI: ");
//
//            if (userInput.equalsIgnoreCase("exit")) {
//                break;
//            }
//
//            // 创建消息
//            JSONObject messageObj = new JSONObject();
//            messageObj.put("role", "user");
//            messageObj.put("content", userInput);
//
//            messages.add(messageObj);
//
//            JSONObject request = new JSONObject();
//            request.put("message", messages);
//
////            System.out.println("messages = " + messages);
//
//            // 发送消息到 WebSocket 服务器
//            client.send(request.toJSONString());  // 使用 FastJSON 序列化
//        }
//
//        scanner.close();
//        client.close();
//    }
//}
