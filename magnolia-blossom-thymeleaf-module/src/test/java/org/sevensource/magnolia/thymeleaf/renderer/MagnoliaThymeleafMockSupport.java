package org.sevensource.magnolia.thymeleaf.renderer;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.template.AreaDefinition;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.rendering.template.assignment.TemplateDefinitionAssignment;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import info.magnolia.rendering.template.type.TemplateTypeHelper;
import info.magnolia.rendering.template.variation.RenderableVariationResolver;
import info.magnolia.rendering.util.AppendableWriter;
import info.magnolia.templating.elements.AreaElement;
import info.magnolia.templating.elements.ComponentElement;
import info.magnolia.templating.elements.PageElement;
import info.magnolia.templating.functions.TemplatingFunctions;

public class MagnoliaThymeleafMockSupport {

	RenderingEngine engine;
	TemplatingFunctions templatingFunctions;
	Node node;
	RenderableDefinition renderableDefinition;
	RenderingContext renderingContext;

	StringWriter stringWriter;

	@Before
	public void before() throws Exception {
		engine = mock(RenderingEngine.class);
		node = mock(Node.class);
		renderableDefinition = mock(RenderableDefinition.class);
		renderingContext = mock(RenderingContext.class);
		final Provider<WebContext> webContextProvider = mock(Provider.class);
		final ServerConfiguration serverConfiguration = mock(ServerConfiguration.class);
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
		
		
		when(webContextProvider.get()).then((i) -> {
			WebContext webCtx = mock(WebContext.class);
			HttpServletResponse response = mock(HttpServletResponse.class);
			HttpServletRequest request = mock(HttpServletRequest.class);
			when(request.getServletContext()).then((j) -> {
				return mock(ServletContext.class);
			});

			when(webCtx.getRequest()).thenReturn(request);
			when(webCtx.getResponse()).thenReturn(response);
			when(webCtx.getLocale()).thenReturn(Locale.ENGLISH);
			
	        AggregationState state = mock(AggregationState.class);
	        //when(state.getMainContentNode()).thenReturn(node);


			when(webCtx.getAggregationState()).thenReturn(state);
			return webCtx;
		});

		templatingFunctions = new TemplatingFunctions(mock(TemplateTypeHelper.class), mock(Provider.class),
				webContextProvider);
		
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
