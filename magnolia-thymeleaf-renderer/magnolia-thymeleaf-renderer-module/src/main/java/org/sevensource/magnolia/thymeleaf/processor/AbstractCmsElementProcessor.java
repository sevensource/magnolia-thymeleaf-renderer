package org.sevensource.magnolia.thymeleaf.processor;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import javax.jcr.Node;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionParser;
import org.thymeleaf.templatemode.TemplateMode;

import info.magnolia.jcr.util.ContentMap;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.templating.elements.AbstractContentTemplatingElement;
import info.magnolia.templating.elements.TemplatingElement;


public abstract class AbstractCmsElementProcessor<T extends TemplatingElement> extends AbstractElementTagProcessor {

	private static final Logger logger = LoggerFactory.getLogger(AbstractCmsElementProcessor.class);
	
    private static final int PRECEDENCE = 1000;
    private final IStandardExpressionParser expressionParser;
    
    protected static final String ATTR_CONTENT = "content";
    protected static final String ATTR_WORKSPACE = "workspace";
    protected static final String ATTR_PATH = "path";
    protected static final String ATTR_UUID = "uuid";


    public AbstractCmsElementProcessor(final TemplateMode templateMode, final String dialectPrefix,
                                       final String elementName) {
    	
    	super(templateMode, dialectPrefix, elementName, (dialectPrefix != null), null, false, PRECEDENCE);
        this.expressionParser = new StandardExpressionParser();
    }

    protected void renderElement(IElementTagStructureHandler structureHandler, TemplatingElement templatingElement) {

        final StringBuilder out = new StringBuilder();
        try {
            templatingElement.begin(out);
            templatingElement.end(out);
        } catch (RenderException | IOException e) {
        	final String msg = String.format("Cannot render element: %s", e.getMessage());
        	logger.error(msg, e);
            throw new TemplateProcessingException(msg, e);
        }
        
        structureHandler.replaceWith(out, false);
    }

    @SuppressWarnings("unchecked")
	protected Class<T> getTemplatingElementClass() {
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    protected final T createTemplatingElement(RenderingContext renderingContext) {
        return Components.getComponentProvider().newInstance(getTemplatingElementClass(), renderingContext);
    }
    
    protected void initContentElement(ITemplateContext context, IProcessableElementTag tag, AbstractContentTemplatingElement element) {
    	final Object contentObject = parseObjectAttribute(context, tag, ATTR_CONTENT);
    	if(contentObject != null) {
    		if (contentObject instanceof Node) {
                element.setContent((Node) contentObject);
            } else if (contentObject instanceof ContentMap) {
                element.setContent(((ContentMap) contentObject).getJCRNode());
            } else {
            	final String msg = String.format(
            			"Don't know how to handle content of type %s", contentObject.getClass().getName());
            	logger.error(msg);
                throw new TemplateProcessingException(msg);
            }
    	}
    	
    	element.setWorkspace(parseStringAttribute(context, tag, ATTR_WORKSPACE));
    	element.setPath(parseStringAttribute(context, tag, ATTR_PATH));
    	element.setNodeIdentifier(parseStringAttribute(context, tag, ATTR_UUID));
    }
    
    protected Boolean parseBooleanAttribute(ITemplateContext context, IProcessableElementTag tag, String attributeName) {
        final String obj = parseStringAttribute(context, tag, attributeName);
        if(obj == null)
        	return null;
        
        return BooleanUtils.toBoolean(obj);
    }
    
    protected Integer parseNumberAttribute(ITemplateContext context, IProcessableElementTag tag, String attributeName) {
        final String obj = parseStringAttribute(context, tag, attributeName);
        if(obj == null)
        	return null;
        
        return NumberUtils.toInt(obj);
    }
    
    protected String parseStringAttribute(ITemplateContext context, IProcessableElementTag tag, String attributeName) {
        final Object obj = parseObjectAttribute(context, tag, attributeName);
        if(obj == null)
        	return null;
        
		if(! (obj instanceof String)) {
        	final String msg = String.format(
        			"Don't know how to handle %s attribute of type %s", attributeName, obj.getClass().getName());
        	logger.error(msg);
            throw new TemplateProcessingException(msg);
        }
        
        return (String) obj;
    }
    
    protected Object parseObjectAttribute(ITemplateContext context, IProcessableElementTag tag, String attributeName) {
    	final String expressionValue = tag.getAttributeValue(getDialectPrefix(), attributeName);
    	
    	if(StringUtils.isBlank(expressionValue)) {
    		return null;
    	}
    	
        final IStandardExpression expression = expressionParser.parseExpression(context, expressionValue);
        final Object obj = expression.execute(context);
        return obj;
    }
}
