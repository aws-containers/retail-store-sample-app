package com.amazon.sample.ui.web.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class CheckoutDeliveryMethodRequest {
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$")
    @NotEmpty
    private String token;
}
