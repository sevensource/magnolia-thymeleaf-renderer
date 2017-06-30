package org.sevensource.magnolia.thymeleaf.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sevensource.magnolia.thymeleaf.workaraounds.AppendableWriterWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.ContentMap;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.renderer.AbstractRenderer;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.rendering.util.AppendableWriter;
import info.magnolia.templating.functions.TemplatingFunctions;

public class ThymeleafRenderer extends AbstractRenderer {

	private static final Logger logger = LoggerFactory.getLogger(ThymeleafRenderer.class);
	
	private final TemplatingFunctions templatingFunctions;
	private final ThymeleafRenderingHelper renderingHelper;
	
	private boolean decodeContent = true;

    @Inject
    public ThymeleafRenderer(RenderingEngine renderingEngine, TemplatingFunctions templatingFunctions) {
        super(renderingEngine);
        this.templatingFunctions = templatingFunctions;
        this.renderingHelper = new ThymeleafRenderingHelper();
    }
    
    @Override
    protected void onRender(Node content, RenderableDefinition definition, RenderingContext renderingCtx,
                            Map<String, Object> ctx, String templateScript) throws RenderException {

        final Map<String, Object> vars = new HashMap<>(ctx);
        
        if(decodeContent) {
        	final Object contentVar = vars.get("content");
        	if(contentVar != null && contentVar instanceof ContentMap) {
        		final ContentMap contentMap = (ContentMap) contentVar;
        		vars.replace("content", templatingFunctions.decode(contentMap));
        	}
        }

        final HttpServletRequest request = MgnlContext.getWebContext().getRequest();
        final HttpServletResponse response = MgnlContext.getWebContext().getResponse();
        final Locale locale = MgnlContext.getAggregationState().getLocale();
        
        try (AppendableWriter out = renderingCtx.getAppendable()) {
        	Writer writerWrapper = new AppendableWriterWrapper(out);
        	renderingHelper.render(request, response, templateScript, locale, vars, writerWrapper);
        } catch (IOException ioe) {
            throw new RenderException(ioe);
        }
    }
    
    @Override
    protected Map<String, Object> newContext() {
        return new HashMap<>();
    }
}
