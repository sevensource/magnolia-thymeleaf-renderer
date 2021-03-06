package org.sevensource.magnolia.thymeleaf.processor;

import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.templating.elements.ComponentElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.Map;

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

public class CmsComponentElementProcessor extends AbstractCmsElementProcessor<ComponentElement> {

	private static final Logger logger = LoggerFactory.getLogger(CmsComponentElementProcessor.class);

	public static final String EL_NAME = "component";

	private final RenderingEngine renderingEngine;

	public CmsComponentElementProcessor(String prefix) {
		super(TemplateMode.HTML, prefix, EL_NAME);
		this.renderingEngine = Components.getComponent(RenderingEngine.class);
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
							 IElementTagStructureHandler structureHandler) {

		final RenderingContext renderingContext = renderingEngine.getRenderingContext();
		final ComponentElement componentElement = createTemplatingElement(renderingContext);
		initTemplatingElement(context, tag, componentElement);
		componentElement.setEditable(parseBooleanAttribute(context, tag, "editable"));
		componentElement.setDialog(parseStringAttribute(context, tag, "dialog"));

		final Map<?, ?> objContextAttributes = parseMapAttribute(context, tag, "contextAttributes");
		if(objContextAttributes != null) {
			@SuppressWarnings("unchecked") final Map<String, Object> ctx = (Map<String, Object>) objContextAttributes;
			componentElement.setContextAttributes(ctx);
		}

		renderElement(structureHandler, componentElement);
	}
}
