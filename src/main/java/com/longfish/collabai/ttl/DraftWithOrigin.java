package com.longfish.collabai.ttl;

import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ClientHandshakeBuilder;

import javax.validation.constraints.NotNull;

@SuppressWarnings("deprecation")
public class DraftWithOrigin extends Draft_17 {

    private final String originUrl;

    public DraftWithOrigin(String originUrl) {
        this.originUrl = originUrl;
    }

    @Override
    public Draft copyInstance() {
        return new DraftWithOrigin(originUrl);
    }

    @NotNull
    @Override
    public ClientHandshakeBuilder postProcessHandshakeRequestAsClient(@NotNull ClientHandshakeBuilder request) {
        super.postProcessHandshakeRequestAsClient(request);
        request.put("Origin", originUrl);
        return request;
    }
}
