package com.amazon.sample.ui.web.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
public class SessionIDWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange,
                             WebFilterChain webFilterChain) {
        String sessionId;

        HttpCookie cookie = serverWebExchange.getRequest().getCookies().getFirst(SessionIDUtil.COOKIE_NAME);

        if(cookie == null) {
            sessionId = SessionIDUtil.addSessionCookie(serverWebExchange);
        }
        else {
            sessionId = cookie.getValue();
        }

        serverWebExchange = serverWebExchange.mutate().request(serverWebExchange.getRequest().mutate().header(SessionIDUtil.HEADER_NAME, sessionId).build()).build();

        return webFilterChain.filter(serverWebExchange);
    }
}
