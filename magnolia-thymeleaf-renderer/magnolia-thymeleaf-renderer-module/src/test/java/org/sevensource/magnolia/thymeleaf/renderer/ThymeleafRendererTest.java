package org.sevensource.magnolia.thymeleaf.renderer;

import static org.junit.Assert.assertTrue;

import java.util.Collections;

import javax.jcr.RepositoryException;

import org.junit.Test;

import info.magnolia.rendering.engine.RenderException;

public class ThymeleafRendererTest extends MagnoliaThymeleafMockSupport {

	
	@Test
	public void test_fragment() throws RenderException, RepositoryException {
		ThymeleafRenderer renderer = new ThymeleafRenderer(engine, templatingFunctions, magnoliaProperties);
		
		renderer.onRender(node, renderableDefinition, renderingContext, Collections.emptyMap(), "test_fragment.html :: main");
		String result = stringWriter.toString();
		assertTrue("fragment was not selected", result.trim().startsWith("<div"));
	}
}
