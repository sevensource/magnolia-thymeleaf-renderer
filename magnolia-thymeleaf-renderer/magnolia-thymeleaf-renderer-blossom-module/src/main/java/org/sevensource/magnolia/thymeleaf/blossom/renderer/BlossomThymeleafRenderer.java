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
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sevensource.magnolia.thymeleaf.renderer.ThymeleafRenderer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.module.blossom.render.RenderContext;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.RenderableDefinition;

public class BlossomThymeleafRenderer extends ThymeleafRenderer implements ApplicationContextAware, MessageSourceAware {

	private final ServletContext servletContext;
	private ApplicationContext applicationContext;
	
    @Inject
    public BlossomThymeleafRenderer(RenderingEngine renderingEngine, ServletContext servletContext, ServerConfiguration serverConfiguration, MagnoliaConfigurationProperties magnoliaProperties) {
        super(renderingEngine, servletContext, serverConfiguration, magnoliaProperties);
        this.servletContext = servletContext;
    }
    
	@Override
	protected TemplateEngine createTemplateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(createTemplateResolver());
		templateEngine.setAdditionalDialects(createDialects());
	    return templateEngine;
	}
    
	@Override
    protected String resolveTemplateScript(Node content, RenderableDefinition definition, RenderingModel<?> model,
    		String actionResult) {
		return RenderContext.get().getTemplateScript();
    }
	
	@Override
	protected void prepareModel(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) {
		
		model.putAll(RenderContext.get().getModel());
		
        final RequestContext requestContext =
                new RequestContext(request, response, servletContext, model);

        addRequestContextAsVariable(model, SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);

        if(applicationContext != null) {
	        // Expose Thymeleaf's own evaluation context as a model variable
	        //
	        // Note Spring's EvaluationContexts are NOT THREAD-SAFE (in exchange for SpelExpressions being thread-safe).
	        // That's why we need to create a new EvaluationContext for each request / template execution, even if it is
	        // quite expensive to create because of requiring the initialization of several ConcurrentHashMaps.
	        final ConversionService conversionService =
	                (ConversionService) request.getAttribute(ConversionService.class.getName()); // might be null!
	        final ThymeleafEvaluationContext evaluationContext =
	                new ThymeleafEvaluationContext(applicationContext, conversionService);
	        model.put(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);
        }
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		ITemplateEngine templateEngine = getTemplateEngine();
		if(templateEngine instanceof MessageSourceAware) {
			((MessageSourceAware) templateEngine).setMessageSource(messageSource);
		}
	}
	
    protected static void addRequestContextAsVariable(
            final Map<String,Object> model, final String variableName, final RequestContext requestContext) {
        
        if (model.containsKey(variableName)) {
            throw new IllegalStateException(
                    "Cannot expose request context in model attribute '" + variableName +
                    "' because of an existing model object of the same name");
        }
        model.put(variableName, requestContext);
    }
}
