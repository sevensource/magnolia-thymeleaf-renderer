package org.sevensource.magnolia.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.templating.elements.PageElement;


public class CmsInitElementProcessor extends AbstractCmsElementProcessor<PageElement> {
	
    private static final String EL_NAME = "init";
    private static final String ATTR_DIALOG = "dialog";
    
    private final RenderingEngine renderingEngine;
    
    public CmsInitElementProcessor(String prefix) {
    	super(TemplateMode.HTML, prefix, EL_NAME);
        this.renderingEngine = Components.getComponent(RenderingEngine.class);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
    		IElementTagStructureHandler structureHandler) {
    	
    	final RenderingContext renderingContext = renderingEngine.getRenderingContext();
        final PageElement pageElement = createTemplatingElement(renderingContext);
        initContentElement(context, tag, pageElement);
        pageElement.setDialog(parseStringAttribute(context, tag, ATTR_DIALOG));
        
        renderElement(structureHandler, pageElement);
    }
}
