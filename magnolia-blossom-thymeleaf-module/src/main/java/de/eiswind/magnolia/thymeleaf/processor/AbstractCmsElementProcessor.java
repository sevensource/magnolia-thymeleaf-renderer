/*
 * Copyright (c) 2014 Thomas Kratz
 *
 This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Dieses Programm ist Freie Software: Sie können es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Wahl) jeder neueren
    veröffentlichten Version, weiterverbreiten und/oder modifizieren.

    Dieses Programm wird in der Hoffnung, dass es nützlich sein wird, aber
    OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
    Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License für weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */

package de.eiswind.magnolia.thymeleaf.processor;

import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.templating.elements.TemplatingElement;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

/**
 * abstract base class for the magnolia element processors.
 *
 * @param <T> the element type.
 */
public abstract class AbstractCmsElementProcessor<T extends TemplatingElement> extends AbstractAttributeTagProcessor {

    private static final int PRECEDENCE = 1000;

    /**
     */

    public AbstractCmsElementProcessor(final TemplateMode templateMode, final String dialectPrefix,
                                       final String elementName, final boolean prefixElementName,
                                       final String attributeName, final boolean prefixAttributeName) {
        super(templateMode, dialectPrefix, elementName,
                prefixElementName, attributeName, prefixAttributeName, PRECEDENCE, true);
    }


    /**
     * create mgnl templating element.
     *
     * @param renderingContext the context
     * @return the teplating element
     */
    protected final T createElement(RenderingContext renderingContext) {
        return Components.getComponentProvider().newInstance(getTemplatingElementClass(), renderingContext);
    }

    /**
     * the type of this element.
     *
     * @return the type
     */
    @SuppressWarnings("unchecked")
    protected final Class<T> getTemplatingElementClass() {
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * mimics mgnl rendering behaviour.
     */
    protected void processElement(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final IElementTagStructureHandler structureHandler,
            final T templatingElement) {

        final StringBuilder out = new StringBuilder();
        try {
            templatingElement.begin(out);
            templatingElement.end(out);
        } catch (RenderException | IOException e) {
            throw new TemplateProcessingException("render area element", e);
        }
        structureHandler.removeAllButFirstChild();
        structureHandler.replaceWith(out, false);
    }
}
