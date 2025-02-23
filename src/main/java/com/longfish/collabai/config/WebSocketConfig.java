//package com.longfish.collabai.config;
//
//import jakarta.websocket.HandshakeResponse;
//import jakarta.websocket.server.HandshakeRequest;
//import jakarta.websocket.server.ServerEndpointConfig;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.server.standard.ServerEndpointExporter;
//
//import java.util.List;
//import java.util.Map;
//
//@Configuration
//public class WebSocketConfig extends ServerEndpointConfig.Configurator {
//
//    @Bean
//    public ServerEndpointExporter serverEndpointExporter() {
//        return new ServerEndpointExporter();
//    }
//
//    @Override
//    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
//        final Map<String, Object> userProperties = sec.getUserProperties();
//        Map<String, List<String>> headers = request.getHeaders();
//        List<String> remoteIp = headers.get("x-forwarded-for");
//        if (remoteIp != null) {
//            userProperties.put("x-forwarded-for", remoteIp.get(0));
//        }
//    }
//
//    @Override
//    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
//        return super.getEndpointInstance(clazz);
//    }
//
//}
