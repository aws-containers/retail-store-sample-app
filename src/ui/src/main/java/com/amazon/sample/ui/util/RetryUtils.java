package com.amazon.sample.ui.util;

import lombok.extern.slf4j.Slf4j;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.net.ConnectException;
import java.time.Duration;

@Slf4j
public class RetryUtils {
    public static RetryBackoffSpec apiClientRetrySpec(String description) {
        return Retry
                .backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> throwable instanceof ConnectException)
                .doBeforeRetry(context -> log.warn("Retrying {}", description));
    }
}
