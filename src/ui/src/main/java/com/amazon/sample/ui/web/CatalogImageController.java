/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.amazon.sample.ui.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * This controller serves product images from the catalog backend, adding cache control headers along the way
 */
@RestController
public class CatalogImageController {

    @Value("${endpoints.assets}")
    private String assetsEndpoint;

    @GetMapping(value = "/assets/{image}", produces = MediaType.IMAGE_JPEG_VALUE)
    public Mono<ResponseEntity<byte[]>> catalogueImage(@PathVariable String image) {
        return WebClient.builder()
            .exchangeStrategies(ExchangeStrategies.builder()
            .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(16 * 1024 * 1024))
            .build())
                .baseUrl(this.assetsEndpoint+"/assets/"+image)
            .build().get()
                .accept(MediaType.IMAGE_JPEG)
                .retrieve()
                .bodyToMono(byte[].class)
                .map(payload -> ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                    .body(payload));
    }
}
