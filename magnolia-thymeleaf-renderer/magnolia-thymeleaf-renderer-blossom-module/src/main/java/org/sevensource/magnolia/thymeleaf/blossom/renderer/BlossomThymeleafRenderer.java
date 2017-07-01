package org.sevensource.magnolia.thymeleaf.blossom.renderer;

import java.util.Map;

import javax.inject.Inject;
import javax.jcr.Node;

import org.sevensource.magnolia.thymeleaf.renderer.ThymeleafRenderer;

import info.magnolia.module.blossom.render.RenderContext;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;

public class BlossomThymeleafRenderer extends ThymeleafRenderer {

    @Inject
    public BlossomThymeleafRenderer(RenderingEngine renderingEngine, TemplatingFunctions templatingFunctions) {
        super(renderingEngine, templatingFunctions);
    }
    
	@Override
    protected String resolveTemplateScript(Node content, RenderableDefinition definition, RenderingModel<?> model,
    		String actionResult) {
		return RenderContext.get().getTemplateScript();
    }
	
	@Override
	protected void setupContext(Map<String, Object> ctx, Node content, RenderableDefinition definition,
			RenderingModel<?> model, Object actionResult) {
        super.setupContext(ctx, content, definition, model, actionResult);
        ctx.putAll(RenderContext.get().getModel());
	}
}
