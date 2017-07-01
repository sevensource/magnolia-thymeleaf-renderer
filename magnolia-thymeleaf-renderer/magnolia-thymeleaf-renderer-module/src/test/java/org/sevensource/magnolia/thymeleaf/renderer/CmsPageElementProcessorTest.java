package org.sevensource.magnolia.thymeleaf.renderer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

import info.magnolia.rendering.engine.RenderException;

public class CmsPageElementProcessorTest extends MagnoliaThymeleafMockSupport {

	@Test
	public void test_init() throws RenderException {
		ThymeleafRenderer renderer = new ThymeleafRenderer(engine, templatingFunctions, magnoliaProperties);
		renderer.onRender(node, renderableDefinition, renderingContext, Collections.emptyMap(), "test_page.html");
		String result = stringWriter.toString();
		assertTrue("cms:page was not rendered", result.contains("<!-- cms:page"));
		assertFalse("xmlns:cms was not removed", result.matches("xmlns:cms=\"[^\"]+\""));
	}
}
