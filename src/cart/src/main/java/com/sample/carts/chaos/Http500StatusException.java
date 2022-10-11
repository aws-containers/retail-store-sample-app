package com.amazon.sample.carts.chaos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Actor Not Found")
public class Http500StatusException extends RuntimeException {
}
