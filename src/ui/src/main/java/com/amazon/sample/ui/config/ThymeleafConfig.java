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

import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
public class ThymeleafConfig {

  @Bean
  public DynamicColSpanDialect dynamicColSpanDialect() {
    return new DynamicColSpanDialect();
  }
}

class DynamicColSpanProcessor extends AbstractAttributeTagProcessor {

  private static final String ATTR_NAME = "dynamic-col-span";
  private static final int PRECEDENCE = 10000;

  public DynamicColSpanProcessor(final String dialectPrefix) {
    super(
      TemplateMode.HTML, // Template mode
      dialectPrefix, // Dialect prefix
      null, // Tag name (null means any)
      false, // Apply to tag end
      ATTR_NAME, // Attribute name
      true, // Remove attribute when processed
      PRECEDENCE, // Precedence
      true // Remove tag if empty
    );
  }

  @Override
  protected void doProcess(
    ITemplateContext context,
    IProcessableElementTag tag,
    AttributeName attributeName,
    String attributeValue,
    IElementTagStructureHandler structureHandler
  ) {
    final IEngineConfiguration configuration = context.getConfiguration();
    final IStandardExpressionParser parser =
      StandardExpressions.getExpressionParser(configuration);

    // Parse and execute the expression
    final IStandardExpression expression = parser.parseExpression(
      context,
      attributeValue
    );
    final String nameToMeasure = (String) expression.execute(context);

    // Calculate col-span based on name length
    var colspanLg = calculateColSpan(nameToMeasure);
    var colspanMd = Math.round(Math.ceil((float) colspanLg / 2));

    String colSpanLgClass = "lg:col-span-" + colspanLg;
    String colSpanMdClass = "md:col-span-" + colspanMd;

    String colSpanClass = colSpanLgClass + " " + colSpanMdClass;

    // Get existing classes
    String existingClasses = tag.getAttributeValue("class");

    // Combine existing classes with new col-span class
    String newClasses = existingClasses != null
      ? existingClasses + " " + colSpanClass
      : colSpanClass;

    // Set the combined classes back to the element
    structureHandler.setAttribute("class", newClasses);
  }

  private long calculateColSpan(String name) {
    if (name == null) {
      return 1;
    }

    var colspanLg = Math.min(
      Math.round(Math.ceil((float) name.length() / 8)),
      8
    );

    return colspanLg;
  }
}

class DynamicColSpanDialect extends AbstractProcessorDialect {

  private static final String DIALECT_NAME = "Dynamic Column Span Dialect";
  private static final String DIALECT_PREFIX = "dcol";

  public DynamicColSpanDialect() {
    super(DIALECT_NAME, DIALECT_PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
  }

  @Override
  public Set<IProcessor> getProcessors(final String dialectPrefix) {
    final Set<IProcessor> processors = new HashSet<>();
    processors.add(new DynamicColSpanProcessor(dialectPrefix));
    return processors;
  }
}
