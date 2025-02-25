package com.amazon.sample.ui.web.dialect;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

public class DynamicColSpanProcessor extends AbstractAttributeTagProcessor {

  private static final int MAX_COLUMNS = 8;
  private static final int MD_COL_RATIO = 2;
  private static final String ATTR_NAME = "dynamic-col-span";
  private static final int PRECEDENCE = 10000;

  public DynamicColSpanProcessor(final String dialectPrefix) {
    super(
      TemplateMode.HTML,
      dialectPrefix,
      null,
      false,
      ATTR_NAME,
      true,
      PRECEDENCE,
      true
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
    var colspanMd = Math.round(Math.ceil((float) colspanLg / MD_COL_RATIO));

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
      Math.round(Math.ceil((float) name.length() / MAX_COLUMNS)),
      MAX_COLUMNS
    );

    return colspanLg;
  }
}
