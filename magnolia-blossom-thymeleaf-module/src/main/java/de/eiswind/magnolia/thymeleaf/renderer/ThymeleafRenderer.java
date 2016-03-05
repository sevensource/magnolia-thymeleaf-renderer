/*
 * Copyright (c) 2014 Thomas Kratz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.eiswind.magnolia.thymeleaf.renderer;

import info.magnolia.context.MgnlContext;
import info.magnolia.module.blossom.render.RenderContext;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.renderer.AbstractRenderer;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.rendering.util.AppendableWriter;
import info.magnolia.templating.functions.TemplatingFunctions;
import info.magnolia.templating.jsp.cmsfn.JspTemplatingFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.context.*;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.*;

import javax.jcr.Node;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * mgnl renderer for thymeleaf.
 */
public class ThymeleafRenderer extends AbstractRenderer implements ServletContextAware, ApplicationContextAware {


    //private final Logger log = LoggerFactory.getLogger(getClass());

    private SpringTemplateEngine engine;

    private ApplicationContext applicationContext;


    private ServletContext servletContext;


    /**
     * Constructs a Renderer that uses Thymeleaf.
     */
    public ThymeleafRenderer() {
        super(Components.getComponent(RenderingEngine.class));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRender(Node content, RenderableDefinition definition, RenderingContext renderingCtx,
                            Map<String, Object> ctx, String templateScript) throws RenderException {

        Map<String, Object> vars = new HashMap<>(ctx);
        vars.put("content", JspTemplatingFunction.asContentMap(content));
        vars.put("cmsfn", Components.getComponent(TemplatingFunctions.class));

        final HttpServletRequest request = MgnlContext.getWebContext().getRequest();
        final HttpServletResponse response = MgnlContext.getWebContext().getResponse();

        // setup spring request context in spring web context
        final RequestContext requestContext = new RequestContext(request, response, servletContext, vars);
        vars.put(SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);

        // copy all spring model attributes into the spring web context as variables
        vars.putAll(RenderContext.get().getModel());


        try (AppendableWriter out = renderingCtx.getAppendable()) {
            // allow template fragment syntax to be used e.g. template.html :: area

            Fragment fragment = computeFragment( templateScript);
            ITemplateContext context = new EngineContext(engine.getConfiguration(), fragment.getTemplateModel().getTemplateData(), new HashMap<>(), Locale.getDefault(), vars);

            // and pass the fragment name and spec then onto the engine
            engine.getConfiguration().getTemplateManager().process(fragment.getTemplateModel(), context, out);
        } catch (IOException x) {
            throw new RenderException(x);
        }


    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> newContext() {
        return new HashMap<String, Object>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String resolveTemplateScript(final Node content, final RenderableDefinition definition,
                                           final RenderingModel<?> model, final String actionResult) {
        return RenderContext.get().getTemplateScript();
    }


    public SpringTemplateEngine getEngine() {
        return engine;
    }

    public void setEngine(final SpringTemplateEngine engine1) {
        this.engine = engine1;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(final ServletContext servletContext1) {
        this.servletContext = servletContext1;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(final ApplicationContext applicationContext1) {
        this.applicationContext = applicationContext1;
    }

    /*
     * This can return a Fragment, NoOpToken (if nothing should be done) or null
     */
    private Fragment computeFragment( final String input) {

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());

        final String trimmedInput = input.trim();


        // If we reached this point, we know for sure this is a complete fragment expression, so we just parse it
        // as such and execute it

        final IStandardExpression fragmentExpression = expressionParser.parseExpression(context, trimmedInput);

        final Object fragmentExpressionResult;

        if (fragmentExpression != null && fragmentExpression instanceof FragmentExpression) {
            // This is not a complex expression but merely a FragmentExpression, so we can apply a shortcut
            // so that we don't require a "null" result for this expression if the template does not exist. That will
            // save a call to resource.exists() which might be costly.

            final FragmentExpression.ExecutedFragmentExpression executedFragmentExpression =
                    FragmentExpression.createExecutedFragmentExpression(context, (FragmentExpression) fragmentExpression, StandardExpressionExecutionContext.NORMAL);

            fragmentExpressionResult =
                    FragmentExpression.resolveExecutedFragmentExpression(context, executedFragmentExpression, true);

        } else {

            fragmentExpressionResult = fragmentExpression.execute(context);

        }


        if (!(fragmentExpressionResult instanceof Fragment)) {
            throw new TemplateProcessingException(
                    "Invalid fragment specification: \"" + input + "\": " +
                            "expression does not return a Fragment object");
        }

        return (Fragment) fragmentExpressionResult;

    }
}
