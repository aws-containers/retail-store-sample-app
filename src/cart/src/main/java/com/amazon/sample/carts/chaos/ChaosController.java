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

package com.amazon.sample.carts.chaos;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chaos")
public class ChaosController {

  private final ChaosService chaosService;

  public ChaosController(ChaosService chaosService) {
    this.chaosService = chaosService;
  }

  @PostMapping("/latency/{ms}")
  public ResponseEntity<Map<String, String>> setLatency(@PathVariable int ms) {
    if (ms < 0) {
      return ResponseEntity.badRequest()
        .body(Map.of("error", "Latency must be a positive number"));
    }
    chaosService.setLatency(ms);
    return ResponseEntity.ok(Map.of("message", "Latency set to " + ms + "ms"));
  }

  @DeleteMapping("/latency")
  public ResponseEntity<Map<String, String>> disableLatency() {
    chaosService.disableLatency();
    return ResponseEntity.ok(Map.of("message", "Latency disabled"));
  }

  @PostMapping("/status/{code}")
  public ResponseEntity<Map<String, String>> setErrorStatus(
    @PathVariable int code
  ) {
    if (HttpStatus.resolve(code) == null) {
      return ResponseEntity.badRequest()
        .body(Map.of("error", "Invalid HTTP status code"));
    }
    chaosService.setErrorStatus(code);
    return ResponseEntity.ok(Map.of("message", "Error status set to " + code));
  }

  @DeleteMapping("/status")
  public ResponseEntity<Map<String, String>> disableErrorStatus() {
    chaosService.disableErrorStatus();
    return ResponseEntity.ok(Map.of("message", "Error status disabled"));
  }

  @PostMapping("/health")
  public ResponseEntity<Map<String, String>> disableHealth() {
    chaosService.setHealth(false);
    return ResponseEntity.ok(Map.of("message", "Health check disabled"));
  }

  @DeleteMapping("/health")
  public ResponseEntity<Map<String, String>> enableHealth() {
    chaosService.setHealth(true);
    return ResponseEntity.ok(Map.of("message", "Health check enabled"));
  }
}
