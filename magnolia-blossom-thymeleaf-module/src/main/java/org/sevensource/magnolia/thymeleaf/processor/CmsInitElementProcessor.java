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

package org.sevensource.magnolia.thymeleaf.processor;

import java.io.IOException;
import java.io.StringWriter;

import javax.jcr.RepositoryException;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.AbstractElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.cms.core.AggregationState;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.cms.security.Permission;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.templating.elements.MarkupHelper;

/**
 * initlializes mgnl stuff on the page.
 */
public class CmsInitElementProcessor extends AbstractElementModelProcessor {

    /**
     * name of the content attribute.
     */
    private static final String CONTENT_ATTRIBUTE = "content";
    private static final String ATTR_NAME = "init";
    private static final int PRECEDENCE = 1000;
    
    private static final String CMS_PAGE_TAG = "cms:page";
    
    private final I18nContentSupport i18nContentSupport;
    private final RenderingEngine renderingEngine;
    
    /**
     * create an instance.
     */
    public CmsInitElementProcessor(String prefix) {
        super(TemplateMode.HTML, prefix, "head", false, ATTR_NAME, true, PRECEDENCE);

        this.i18nContentSupport = Components.getComponent(I18nContentSupport.class);
        this.renderingEngine = Components.getComponent(RenderingEngine.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcess(ITemplateContext context,
                             IModel model,
                             IElementModelStructureHandler structureHandler) {
        AggregationState aggregationState = MgnlContext.getAggregationState();
        javax.jcr.Node activePage = aggregationState.getMainContentNode();


        ServerConfiguration config = Components.getComponent(ServerConfiguration.class);
        boolean isAdmin = config.isAdmin()
                && !aggregationState.isPreviewMode()
                && activePage != null
                && NodeUtil.isGranted(activePage, Permission.SET);

        if (!isAdmin) {
            return;
        }

        final TemplateMode templateMode = context.getTemplateMode();
        final IModelFactory modelFactory =
                context.getConfiguration().getModelFactory(templateMode);
        
        IOpenElementTag head = (IOpenElementTag) model.get(0);
        head = modelFactory.removeAttribute(head, getDialectPrefix() + ":init");
        model.replace(0, head);
        
        IStandaloneElementTag meta = modelFactory.createStandaloneElementTag("meta");
        meta = modelFactory.setAttribute(meta, "gwt:property", "locale=" + i18nContentSupport.getLocale());
        model.insert(model.size() - 1, meta); // insert before closing head tag

        StringWriter writer = new StringWriter();
        MarkupHelper helper = new MarkupHelper(writer);
        try {
            helper.append(" " + CMS_PAGE_TAG);
            helper.attribute(CONTENT_ATTRIBUTE, getNodePath(activePage));

            
            final RenderingContext renderingContext = renderingEngine.getRenderingContext();
            final TemplateDefinition templateDefinition = (TemplateDefinition) renderingContext.getRenderableDefinition();
            final String dlg = templateDefinition.getDialog();
            if (dlg != null) {
                helper.attribute("dialog", dlg);
            }
            helper.attribute("preview", String.valueOf(MgnlContext.getAggregationState().isPreviewMode()));
        } catch (IOException e) {
            throw new TemplateProcessingException("comment", e);
        }

        model.insert(model.size() - 1, modelFactory.createText("\n"));
        IComment comment = modelFactory.createComment(writer.toString());
        model.insert(model.size() - 1, comment);
        
        model.insert(model.size() - 1, modelFactory.createText("\n"));
        comment = modelFactory.createComment(" /" + CMS_PAGE_TAG + " ");
        model.insert(model.size() - 1, comment);
        
        model.insert(model.size() - 1, modelFactory.createText("\n"));
    }

    /**
     * the path to a node.
     *
     * @param node the node
     * @return its path
     * @throws TemplateProcessingException wraps repo exceptions
     */
    protected String getNodePath(javax.jcr.Node node) throws TemplateProcessingException {
        try {
            return node.getSession().getWorkspace().getName() + ":" + node.getPath();
        } catch (RepositoryException e) {
            throw new TemplateProcessingException("Can't construct node path for node " + node, e);
        }
    }
}
