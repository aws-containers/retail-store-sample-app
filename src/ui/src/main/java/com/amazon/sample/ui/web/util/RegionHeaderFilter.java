package com.amazon.sample.ui.web.util;

import com.amazon.sample.ui.services.Metadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

//@Component
@Slf4j
public class RegionHeaderFilter implements WebFilter {

    @Autowired
    private Metadata metadata;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange,
                             WebFilterChain webFilterChain) {
            serverWebExchange.getResponse()
                    .getHeaders().add("X-Retail-Store-Metadata", this.metadata.toString());

        return webFilterChain.filter(serverWebExchange);
    }
}
