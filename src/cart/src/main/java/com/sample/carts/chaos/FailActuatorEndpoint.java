package com.amazon.sample.carts.chaos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id="fail")
public class FailActuatorEndpoint {
    @Autowired
    private FailHealthIndicator indicator;

    @WriteOperation
    public String activate() {
        this.indicator.fail();

        return "OK";
    }
}
