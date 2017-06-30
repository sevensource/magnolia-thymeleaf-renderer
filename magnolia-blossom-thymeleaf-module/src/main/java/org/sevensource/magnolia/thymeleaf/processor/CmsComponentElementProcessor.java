package org.sevensource.magnolia.thymeleaf.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.templating.elements.ComponentElement;


public class CmsComponentElementProcessor extends AbstractCmsElementProcessor<ComponentElement> {

	private static final Logger logger = LoggerFactory.getLogger(CmsComponentElementProcessor.class);
	
    public static final String EL_NAME = "component";
    
    private final RenderingEngine renderingEngine;
    
    public CmsComponentElementProcessor(String prefix) {
        super(TemplateMode.HTML, prefix, EL_NAME);
        this.renderingEngine = Components.getComponent(RenderingEngine.class);
    }
    
    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
    		IElementTagStructureHandler structureHandler) {
    
        final RenderingContext renderingContext = renderingEngine.getRenderingContext();
        final ComponentElement componentElement = createTemplatingElement(renderingContext);
        initContentElement(context, tag, componentElement);
        componentElement.setEditable(parseBooleanAttribute(context, tag, "editable"));
        componentElement.setDialog(parseStringAttribute(context, tag, "dialog"));
        
//      Map<String, Object> contextAttributes = (Map<String, Object>) object(params, "contextAttributes");
//      templatingElement.setContextAttributes(contextAttributes);
        
        renderElement(structureHandler, componentElement);
    }
}
