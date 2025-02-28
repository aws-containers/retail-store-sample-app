package com.amazon.sample.ui.web.dialect;

import com.amazon.sample.ui.web.util.TopologyInformation;
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

public class TopologyInformationStatusProcessor
  extends AbstractAttributeTagProcessor {

  private static final String ATTR_NAME = "status";
  private static final int PRECEDENCE = 10000;

  public TopologyInformationStatusProcessor(final String dialectPrefix) {
    super(
      TemplateMode.HTML, // Template mode
      dialectPrefix, // Prefix to be used
      null, // Tag name (null means any tag)
      false, // Apply to the element containing the attribute
      ATTR_NAME, // Attribute name
      true, // Attribute must be removed when processed
      PRECEDENCE, // Precedence
      true // Remove the attribute once processed
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
    final TopologyInformation topologyInformation =
      (TopologyInformation) expression.execute(context);
    final String statusValue = topologyInformation.getStatus().name();

    String cssClass = tag.getAttributeValue("class");

    String extraClasses = " bg-gray-100 text-gray-800";

    switch (statusValue) {
      case "HEALTHY":
        extraClasses = " bg-green-100 text-green-800";
        break;
      case "UNHEALTHY":
        extraClasses = " bg-red-100 text-red-800";
        break;
      default:
        break;
    }

    structureHandler.setAttribute("class", cssClass + extraClasses);
    structureHandler.setBody(statusValue, false);
  }
}
