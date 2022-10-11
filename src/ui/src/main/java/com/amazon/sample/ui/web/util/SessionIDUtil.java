package com.amazon.sample.ui.web.util;

import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.UUID;

public class SessionIDUtil {
    public static final String COOKIE_NAME = "SESSIONID";

    public static final String HEADER_NAME = "X-Session-ID";

    public static String addSessionCookie(ServerWebExchange exchange) {
        String sessionId = UUID.randomUUID().toString();

        ResponseCookie newCookie = ResponseCookie.from(COOKIE_NAME, sessionId).build();

        exchange.getResponse()
                .getCookies().add(COOKIE_NAME, newCookie);

        return sessionId;
    }

    public static String getSessionId(ServerHttpRequest request) {
        return request.getHeaders().getFirst(HEADER_NAME);
    }
}
