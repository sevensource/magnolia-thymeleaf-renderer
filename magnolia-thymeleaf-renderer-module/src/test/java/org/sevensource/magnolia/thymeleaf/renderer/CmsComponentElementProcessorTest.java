package org.sevensource.magnolia.thymeleaf.renderer;

import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

import info.magnolia.rendering.engine.RenderException;

public class CmsComponentElementProcessorTest extends MagnoliaThymeleafMockSupport {
	
	@Test
	public void test_component() throws RenderException {
		ThymeleafRenderer renderer = new ThymeleafRenderer(engine, templatingFunctions);
		renderer.onRender(node, renderableDefinition, renderingContext, Collections.emptyMap(), "test_component.html");
		String result = stringWriter.toString();
		assertTrue("cms:area was not rendered", result.contains("<!-- cms:area"));		
	}
}
