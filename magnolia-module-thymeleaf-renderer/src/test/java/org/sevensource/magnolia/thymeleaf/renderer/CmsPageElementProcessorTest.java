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

import info.magnolia.rendering.engine.RenderException;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CmsPageElementProcessorTest extends MagnoliaThymeleafMockSupport {

	@Test
	public void test_init() throws RenderException {
		ThymeleafRenderer renderer = new ThymeleafRenderer(engine, servletContext, serverConfiguration, magnoliaProperties);
		renderer.onRender(node, renderableDefinition, renderingContext, Collections.emptyMap(), "test_page.html");
		String result = stringWriter.toString();
		assertTrue("cms:page was not rendered", result.contains("<!-- cms:page"));
		assertFalse("xmlns:cms was not removed", result.matches("xmlns:cms=\"[^\"]+\""));
	}
}
