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


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Provider;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;

import info.magnolia.beanmerger.BeanMerger;
import info.magnolia.beanmerger.ProxyBasedBeanMerger;
import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.cms.core.AggregationState;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.i18nsystem.I18nizer;
import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.template.AreaDefinition;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.rendering.template.assignment.TemplateDefinitionAssignment;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import info.magnolia.rendering.template.variation.RenderableVariationResolver;
import info.magnolia.rendering.util.AppendableWriter;
import info.magnolia.templating.elements.AreaElement;
import info.magnolia.templating.elements.ComponentElement;
import info.magnolia.templating.elements.PageElement;

public class MagnoliaThymeleafMockSupport {

	RenderingEngine engine;
	Node node;
	RenderableDefinition renderableDefinition;
	RenderingContext renderingContext;
	ServletContext servletContext;
	ServerConfiguration serverConfiguration;
	
	MagnoliaConfigurationProperties magnoliaProperties;

	StringWriter stringWriter;

	@Before
	public void before() throws Exception {
		engine = mock(RenderingEngine.class);
		node = mock(Node.class);
		renderableDefinition = mock(RenderableDefinition.class);
		renderingContext = mock(RenderingContext.class);
		magnoliaProperties = mock(MagnoliaConfigurationProperties.class);
		serverConfiguration = mock(ServerConfiguration.class);
		final Provider<WebContext> webContextProvider = mock(Provider.class);
		final ComponentProvider componentProvider = mock(ComponentProvider.class);
		
		when(engine.getRenderingContext()).then((i) -> renderingContext) ;
		
		
		
		when(node.getSession()).then((i) -> {
			Workspace workspace = mock(Workspace.class);
			when(workspace.getName()).thenReturn("pages");
			
			Session session = mock(Session.class);
			when(session.getWorkspace()).thenReturn(workspace);
			when(session.hasPermission(any(), any())).thenReturn(true);
			
			
			return session;
		});
		
		when(node.getPath()).thenReturn("/home");
		when(node.getNodes()).then((i) -> {
	      NodeIterator nodeIterator = mock(NodeIterator.class);
	      when(nodeIterator.hasNext()).thenReturn(false);
	      return nodeIterator;
		});
		
		
		servletContext = mock(ServletContext.class);
		
		when(webContextProvider.get()).then((i) -> {
			WebContext webCtx = mock(WebContext.class);
			HttpServletResponse response = mock(HttpServletResponse.class);
			HttpServletRequest request = mock(HttpServletRequest.class);
			
			Map<String, Object> requestAttributes = new HashMap<>();
			
			when(request.getServletContext()).then((j) -> servletContext);
			when(request.getAttribute(any())).then((j) -> requestAttributes.get(j.getArgumentAt(0, String.class)));
			
			
			doAnswer((j) -> requestAttributes.put(j.getArgumentAt(0, String.class), j.getArgumentAt(1, Object.class))).
			when(request).setAttribute(any(), any());
			
			when(request.getAttributeNames()).then((j) -> requestAttributes.keySet());

			when(webCtx.getRequest()).thenReturn(request);
			when(webCtx.getResponse()).thenReturn(response);
			when(webCtx.getLocale()).thenReturn(Locale.ENGLISH);
			
	        AggregationState state = mock(AggregationState.class);
	        //when(state.getMainContentNode()).thenReturn(node);


			when(webCtx.getAggregationState()).thenReturn(state);
			return webCtx;
		});
		
		TemplateDefinition templateDefinition = mock(ConfiguredTemplateDefinition.class);
		when(templateDefinition.getDialog()).thenReturn(null);
		when(templateDefinition.getAreas()).then((j) -> {
	        final AreaDefinition areaDefinition = mock(AreaDefinition.class);
	        when(areaDefinition.getName()).thenReturn("main");
	        when(areaDefinition.getEnabled()).thenReturn(true);
	        
	        final Map<String, AreaDefinition> areas = new HashMap<>();
	        areas.put("main", areaDefinition);
			return areas;
		});
		
		
		when(renderingContext.getAppendable()).then((i) -> {
			stringWriter = new StringWriter();
			AppendableWriter out = new AppendableWriter(stringWriter);
			return out;
		});
		when(renderingContext.getRenderableDefinition()).then((i) -> {
			return templateDefinition;
		});
		when(renderingContext.getCurrentContent()).then((i) -> node);

		
		when(serverConfiguration.isAdmin()).thenReturn(true);

		
		when(componentProvider.getComponent(eq(RenderingEngine.class))).thenReturn(engine);

		
		when(componentProvider.newInstance(eq(PageElement.class), any())).then((i) -> {
			PageElement element = new PageElement(serverConfiguration, renderingContext);
			return element;
		});
		when(componentProvider.newInstance(eq(AreaElement.class), any())).then((i) -> {
			RenderableVariationResolver variationResolver = mock(RenderableVariationResolver.class);
			
			I18nizer i18nizer = mock(I18nizer.class);
			when(i18nizer.decorate(templateDefinition)).thenReturn(templateDefinition);
			
			AreaElement element = new AreaElement(serverConfiguration, renderingContext, engine, variationResolver, i18nizer);
			return element;
		});
		when(componentProvider.newInstance(eq(ComponentElement.class), any())).then((i) -> {
			I18nizer i18nizer = mock(I18nizer.class);			
			when(i18nizer.decorate(templateDefinition)).thenReturn(templateDefinition);
			
			TemplateDefinitionAssignment templateDefinitionAssignment = mock(TemplateDefinitionAssignment.class);
			when(templateDefinitionAssignment.getAssignedTemplateDefinition(any())).then((j) -> templateDefinition);
			ComponentElement element = new ComponentElement(serverConfiguration, renderingContext, engine, templateDefinitionAssignment, i18nizer, webContextProvider.get());
			return element;
		});
		when(componentProvider.getComponent(BeanMerger.class)).thenReturn(new ProxyBasedBeanMerger());
		
		Components.setComponentProvider(componentProvider);
		MgnlContext.setInstance(webContextProvider.get());
	}
	
    @After
    public void cleanup() {
        Components.popProvider();
    }
}
