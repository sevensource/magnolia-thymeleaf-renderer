package org.sevensource.magnolia.thymeleaf.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.template.AreaDefinition;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import info.magnolia.templating.elements.AreaElement;


public class CmsAreaElementProcessor extends AbstractCmsElementProcessor<AreaElement> {
	
	private static final Logger logger = LoggerFactory.getLogger(CmsAreaElementProcessor.class);

    private static final String EL_NAME = "area";
    private static final String ATTR_NAME = "name";

    private final RenderingEngine renderingEngine;
    
    
    public CmsAreaElementProcessor(String prefix) {
        super(TemplateMode.HTML, prefix, EL_NAME);
        this.renderingEngine = Components.getComponent(RenderingEngine.class);
    }
    
    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
    		IElementTagStructureHandler structureHandler) {
    	
    	final RenderingContext renderingContext = renderingEngine.getRenderingContext();
        final ConfiguredTemplateDefinition templateDefinition = getTemplateDefintion(renderingContext);
        
        final String areaName = parseStringAttribute(context, tag, ATTR_NAME);
        
        if (! templateDefinition.getAreas().containsKey(areaName)) {
        	final String msg = String.format("Area '%s' not found", areaName);
        	logger.error(msg);
        	throw new TemplateProcessingException(msg);
        }
        
        
        final AreaDefinition areaDefinition = templateDefinition.getAreas().get(areaName);
        final AreaElement areaElement = createTemplatingElement(renderingContext);
        initContentElement(context, tag, areaElement);
        areaElement.setName(areaDefinition.getName());
        
        areaElement.setAvailableComponents(parseStringAttribute(context, tag, "components"));
        areaElement.setDialog(parseStringAttribute(context, tag, "dialog"));
        areaElement.setType(parseStringAttribute(context, tag, "type"));
        areaElement.setLabel(parseStringAttribute(context, tag, "label"));
        areaElement.setDescription(parseStringAttribute(context, tag, "description"));
        areaElement.setEditable(parseBooleanAttribute(context, tag, "editable"));
        areaElement.setCreateAreaNode(parseBooleanAttribute(context, tag, "createAreaNode"));
        areaElement.setMaxComponents(parseNumberAttribute(context, tag, "maxComponents"));
        
//        Map<String, Object> contextAttributes = (Map<String, Object>) object(params, "contextAttributes");
//        templatingElement.setContextAttributes(contextAttributes);
        
        renderElement(structureHandler, areaElement);
    }
    
    
    private ConfiguredTemplateDefinition getTemplateDefintion(RenderingContext renderingContext) {
    	final RenderableDefinition renderableDefinition = renderingContext.getRenderableDefinition();
        if(! (renderableDefinition instanceof ConfiguredTemplateDefinition)) {
        	final String msg = String.format(
        			"RenderableDefinition is of type %s. Only ConfiguredTemplateDefinition is supported for areas.",
        			renderableDefinition.getClass().getName());
        	logger.error(msg);
        	throw new TemplateProcessingException(msg);
        }
        
        return (ConfiguredTemplateDefinition) renderableDefinition;
    }
}
