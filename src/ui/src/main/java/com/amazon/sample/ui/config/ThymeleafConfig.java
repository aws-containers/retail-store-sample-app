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

package com.amazon.sample.ui.config;

import com.amazon.sample.ui.web.dialect.DynamicColSpanProcessor;
import com.amazon.sample.ui.web.dialect.TopologyInformationStatusProcessor;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

@Configuration
public class ThymeleafConfig {

  @Bean
  public DynamicColSpanDialect dynamicColSpanDialect() {
    return new DynamicColSpanDialect();
  }
}

class DynamicColSpanDialect extends AbstractProcessorDialect {

  private static final String DIALECT_NAME = "Retail Dialect";
  private static final String DIALECT_PREFIX = "retail";

  DynamicColSpanDialect() {
    super(DIALECT_NAME, DIALECT_PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
  }

  @Override
  public Set<IProcessor> getProcessors(final String dialectPrefix) {
    final Set<IProcessor> processors = new HashSet<>();
    processors.add(new DynamicColSpanProcessor(dialectPrefix));
    processors.add(new TopologyInformationStatusProcessor(dialectPrefix));
    return processors;
  }
}
