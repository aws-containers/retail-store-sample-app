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

import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ChaosService {

  private int latency = 0;
  private int errorStatus = 0;
  private boolean isLatencyEnabled = false;
  private boolean isErrorStatusEnabled = false;
  private boolean isHealthy = true;

  public void setLatency(int milliseconds) {
    this.latency = milliseconds;
    this.isLatencyEnabled = true;
  }

  public void setErrorStatus(int status) {
    this.errorStatus = status;
    this.isErrorStatusEnabled = true;
  }

  public void disableLatency() {
    this.isLatencyEnabled = false;
  }

  public void disableErrorStatus() {
    this.isErrorStatusEnabled = false;
  }

  public void setHealth(boolean healthy) {
    this.isHealthy = healthy;
  }

  public boolean isHealthy() {
    return isHealthy;
  }

  public Optional<Integer> getLatencyDelay() {
    return isLatencyEnabled ? Optional.of(latency) : Optional.empty();
  }

  public Optional<Integer> getErrorStatus() {
    return isErrorStatusEnabled ? Optional.of(errorStatus) : Optional.empty();
  }
}
