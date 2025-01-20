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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/utility")
public class UtilityController {

  private static final double MONTE_CARLO_CONSTANT = 4.0;

  @Autowired
  private ToggleHealthIndicator healthIndicator;

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
}
