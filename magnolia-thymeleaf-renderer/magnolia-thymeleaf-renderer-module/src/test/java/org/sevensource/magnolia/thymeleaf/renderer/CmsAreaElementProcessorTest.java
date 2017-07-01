package org.sevensource.magnolia.thymeleaf.renderer;

import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

import info.magnolia.rendering.engine.RenderException;

public class CmsAreaElementProcessorTest extends MagnoliaThymeleafMockSupport {
	
	@Test
	public void test_area() throws RenderException {
		ThymeleafRenderer renderer = new ThymeleafRenderer(engine, templatingFunctions, magnoliaProperties);
		renderer.init();
		renderer.onRender(node, renderableDefinition, renderingContext, Collections.emptyMap(), "test_area.html");
		String result = stringWriter.toString();
		assertTrue("cms:area was not rendered", result.contains("<!-- cms:area"));		
	}
}
