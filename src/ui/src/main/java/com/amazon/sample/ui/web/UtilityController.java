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

import com.amazon.sample.ui.util.ToggleHealthIndicator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/utility")
public class UtilityController {

  private final ApplicationContext context;

  private static final double MONTE_CARLO_CONSTANT = 4.0;
  private static final int PANIC_DELAY = 500;
  private static final int PANIC_EXIT_CODE = 255;

  @Autowired
  private ToggleHealthIndicator healthIndicator;

  public UtilityController(ApplicationContext context) {
    this.context = context;
  }

  @GetMapping("/stress/{iterations}")
  @ResponseBody
  public double stress(@PathVariable int iterations) {
    return monteCarloPi(iterations);
  }

  private double monteCarloPi(int iterations) {
    int inside = 0;
    for (int i = 0; i < iterations; i++) {
      double x = Math.random();
      double y = Math.random();
      if (Math.sqrt(x * x + y * y) < 1.0) {
        inside++;
      }
    }

    return (MONTE_CARLO_CONSTANT * inside) / iterations;
  }

  @GetMapping("/status/{code}")
  public ResponseEntity<String> status(@PathVariable int code) {
    return ResponseEntity.status(code).body("OK");
  }

  @PostMapping("/health/up")
  public ResponseEntity<String> healthUp() {
    return this.toggleHealth(true);
  }

  @PostMapping("/health/down")
  public ResponseEntity<String> healthDown() {
    return this.toggleHealth(false);
  }

  private ResponseEntity<String> toggleHealth(@RequestParam boolean healthy) {
    healthIndicator.setHealth(healthy);
    String status = healthy ? "UP" : "DOWN";
    return ResponseEntity.ok("Health status set to: " + status);
  }

  @GetMapping(value = "/headers", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, List<String>>> statusHeaders(
    @RequestHeader HttpHeaders headers
  ) {
    return ResponseEntity.ok().body(headers);
  }

  @GetMapping("/panic")
  public ResponseEntity<String> panic() {
    Thread thread = new Thread(() -> {
      try {
        Thread.sleep(PANIC_DELAY); // Small delay to allow response to be sent
        SpringApplication.exit(context, () -> 1);
        System.exit(PANIC_EXIT_CODE);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });
    thread.start();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      "Shutting down..."
    );
  }

  // function /echo that answer the hash provided as POST input
  @PostMapping("/echo")
  @ResponseBody
  public ResponseEntity<String> echo(@RequestBody String body) {
    return ResponseEntity.ok().body(body);
  }

  // function /store what take a POST hash to write to a locally created file
  @PostMapping("/store")
  @ResponseBody
  public ResponseEntity<String> store(@RequestBody String body) {
    String filename = String.valueOf(Math.abs(body.hashCode())); // ensure positive number
    try (
      java.io.FileWriter fileWriter = new java.io.FileWriter(
        "/tmp/" + filename + ".json"
      )
    ) {
      fileWriter.write(body);
      return ResponseEntity.ok().body("{\"hash\": \"" + filename + "\"}");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        "Error writing file: " + e.getMessage()
      );
    }
  }

  // function /store/{hash} that read a local hash file
  @GetMapping("/store/{hash}")
  @ResponseBody
  public ResponseEntity<String> read(@PathVariable String hash) {
    // Validate hash format - only allow numeric characters
    if (!hash.matches("^[0-9]+$")) {
      return ResponseEntity.badRequest().body("Invalid hash format");
    }

    // Normalize the path and verify it's within the intended directory
    Path filePath = Paths.get("/tmp", hash + ".json").normalize();
    if (!filePath.startsWith("/tmp/")) {
      return ResponseEntity.badRequest().body("Invalid path");
    }

    try {
      String body = new String(Files.readAllBytes(filePath));
      return ResponseEntity.ok().body(body);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        "Error reading file: " + e.getMessage()
      );
    }
  }
}
