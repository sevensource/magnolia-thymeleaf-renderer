package org.sevensource.magnolia.thymeleaf.renderer;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.junit.Before;
import org.junit.Test;

import info.magnolia.rendering.engine.RenderException;

public class CmsComponentElementProcessorTest extends MagnoliaThymeleafMockSupport {
	
	Node content;
	
	@Before
	public void beforeEach() throws RepositoryException {
		content = mock(Node.class);
		when(content.getSession()).then((i) -> {
			Workspace workspace = mock(Workspace.class);
			when(workspace.getName()).thenReturn("pages");
			
			Session session = mock(Session.class);
			when(session.getWorkspace()).thenReturn(workspace);
			when(session.hasPermission(any(), any())).thenReturn(true);
			
			
			return session;
		});
		
		when(content.getPath()).thenReturn("/home");
		when(content.getNodes()).then((i) -> {
	      NodeIterator nodeIterator = mock(NodeIterator.class);
	      when(nodeIterator.hasNext()).thenReturn(false);
	      return nodeIterator;
		});
	}
	
	@Test
	public void test_component() throws RenderException, RepositoryException {
		ThymeleafRenderer renderer = new ThymeleafRenderer(engine, templatingFunctions, magnoliaProperties);
		
		Map<String, Object> variables = new HashMap<>();
		variables.put("someContent", content);
		
		renderer.onRender(node, renderableDefinition, renderingContext, variables, "test_component.html");
		String result = stringWriter.toString();
		assertTrue("cms:component was not rendered", result.contains("<!-- cms:component"));		
	}
}
