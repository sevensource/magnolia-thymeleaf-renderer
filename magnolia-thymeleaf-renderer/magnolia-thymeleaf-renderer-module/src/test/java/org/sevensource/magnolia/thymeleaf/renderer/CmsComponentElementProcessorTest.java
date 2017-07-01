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
