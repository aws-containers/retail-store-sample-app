package com.amazon.sample.carts.chaos;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class FailHealthIndicator implements HealthIndicator {

    private boolean down = false;

    public void fail() {
        this.down = true;
    }

    @Override
    public Health health() {
        return this.down ? Health.down().build() : Health.up().build();
    }
}
