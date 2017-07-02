package org.sevensource.magnolia.thymeleaf.blossom.renderer;

/*-
 * #%L
 * Magnolia Thymeleaf Blossom Renderer
 * %%
 * Copyright (C) 2017 SevenSource, pgaschuetz
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.Map;

import javax.inject.Inject;
import javax.jcr.Node;

import org.sevensource.magnolia.thymeleaf.renderer.ThymeleafRenderer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;

import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.module.blossom.render.RenderContext;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.RenderableDefinition;

public class BlossomThymeleafRenderer extends ThymeleafRenderer {

    @Inject
    public BlossomThymeleafRenderer(RenderingEngine renderingEngine, MagnoliaConfigurationProperties magnoliaProperties) {
        super(renderingEngine, magnoliaProperties);
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

	@Override
	protected TemplateEngine getTemplateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(getTemplateResolver());
		templateEngine.setAdditionalDialects(getDialects());
	    return templateEngine;
	}
}
