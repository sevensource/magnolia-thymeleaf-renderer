package org.sevensource.magnolia.thymeleaf.renderer;

/*-
 * #%L
 * Magnolia Thymeleaf Renderer
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

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sevensource.magnolia.thymeleaf.dialect.MagnoliaDialect;
import org.sevensource.magnolia.thymeleaf.workaraounds.AppendableWriterWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import info.magnolia.context.MgnlContext;
import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.jcr.util.ContentMap;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.renderer.AbstractRenderer;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.rendering.util.AppendableWriter;
import info.magnolia.templating.functions.TemplatingFunctions;

public class ThymeleafRenderer extends AbstractRenderer {

	private static final Logger logger = LoggerFactory.getLogger(ThymeleafRenderer.class);
	
	private final TemplatingFunctions templatingFunctions;
	private ThymeleafRenderingHelper renderingHelper;
	
	private boolean decodeContent = true;
	private boolean cacheTemplates = true;

    @Inject
    public ThymeleafRenderer(RenderingEngine renderingEngine, TemplatingFunctions templatingFunctions, MagnoliaConfigurationProperties magnoliaProperties) {
        super(renderingEngine);
        this.templatingFunctions = templatingFunctions;
        final boolean devMode = magnoliaProperties.getBooleanProperty("magnolia.develop");
        this.cacheTemplates = !devMode;
        this.renderingHelper = new ThymeleafRenderingHelper(getTemplateEngine());
    }
    
    @Override
    protected void onRender(Node content, RenderableDefinition definition, RenderingContext renderingCtx,
                            Map<String, Object> ctx, String templateScript) throws RenderException {
    	
        final Map<String, Object> vars = new HashMap<>(ctx);
        
        if(decodeContent) {
        	final Object contentVar = vars.get("content");
        	if(contentVar != null && contentVar instanceof ContentMap) {
        		final ContentMap contentMap = (ContentMap) contentVar;
        		vars.replace("content", templatingFunctions.decode(contentMap));
        	}
        }
        
        final HttpServletRequest request = MgnlContext.getWebContext().getRequest();
        final HttpServletResponse response = MgnlContext.getWebContext().getResponse();
        final Locale locale = MgnlContext.getAggregationState().getLocale();
        
        try (AppendableWriter out = renderingCtx.getAppendable()) {
        	Writer writerWrapper = new AppendableWriterWrapper(out);
        	renderingHelper.render(request, response, templateScript, locale, vars, writerWrapper);
        } catch (IOException ioe) {
            throw new RenderException(ioe);
        }
    }
    
    @Override
    protected Map<String, Object> newContext() {
        return new HashMap<>();
    }
    
    protected ITemplateEngine getTemplateEngine() {
    	TemplateEngine templateEngine = new TemplateEngine();
    	templateEngine.setTemplateResolver(getTemplateResolver());
    	templateEngine.setAdditionalDialects(getDialects());
    	return templateEngine;
    }
    
    protected ITemplateResolver getTemplateResolver() {
    	ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
    	resolver.setTemplateMode(TemplateMode.HTML);
    	resolver.setCacheable(this.cacheTemplates);
    	return resolver;
    }
    
    protected Set<IDialect> getDialects() {
    	final Set<IDialect> dialects = new HashSet<>();
    	dialects.add(new MagnoliaDialect());
    	
    	try {
    		Class<?> java8TimeDialectClazz = Class.forName("org.thymeleaf.extras.java8time.dialect.Java8TimeDialect");
    		IDialect java8TimeDialect = (IDialect) java8TimeDialectClazz.newInstance();
    		dialects.add(java8TimeDialect);
    	} catch(ClassNotFoundException e) {
    		logger.trace("Did not find Java8TimeDialect");
    	} catch (IllegalAccessException | InstantiationException e) {
    		logger.error("Cannot create Java8TimeDialect", e);
		}
    	
    	return dialects;
    }
    
    public void setDecodeContent(boolean decodeContent) {
		this.decodeContent = decodeContent;
	}
}
