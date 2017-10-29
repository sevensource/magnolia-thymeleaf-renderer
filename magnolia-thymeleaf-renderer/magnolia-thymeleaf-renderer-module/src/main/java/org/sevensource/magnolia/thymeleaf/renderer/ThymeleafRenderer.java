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

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sevensource.magnolia.thymeleaf.dialect.MagnoliaDialect;
import org.sevensource.magnolia.thymeleaf.workaraounds.AppendableWriterWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebExpressionContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.context.MgnlContext;
import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.rendering.util.AppendableWriter;

public class ThymeleafRenderer extends AbstractThymeleafRenderer {

	private static final String MAGNOLIA_DEVELOP_PROPERTY = "magnolia.develop";

	private static final Logger logger = LoggerFactory.getLogger(ThymeleafRenderer.class);

	private ServletContext servletContext;
	private TemplateEngine templateEngine;
	private boolean cacheTemplates = true;
	private Set<String> extraDialects = new HashSet<>();

	@Inject
	public ThymeleafRenderer(RenderingEngine renderingEngine, ServletContext servletContext,
			ServerConfiguration serverConfiguration, MagnoliaConfigurationProperties magnoliaProperties) {
		super(renderingEngine, serverConfiguration);
		final boolean devMode = magnoliaProperties.getBooleanProperty(MAGNOLIA_DEVELOP_PROPERTY);
		this.cacheTemplates = !devMode;
		this.templateEngine = createTemplateEngine();
		this.servletContext = servletContext;
	}

	protected TemplateEngine createTemplateEngine() {
		final TemplateEngine engine = new TemplateEngine();
		engine.setTemplateResolver(createTemplateResolver());
		engine.setAdditionalDialects(createDialects());
		return engine;
	}

	protected ITemplateResolver createTemplateResolver() {
		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setCacheable(this.cacheTemplates);
		return resolver;
	}

	protected Set<IDialect> createDialects() {
		final Set<IDialect> dialects = new HashSet<>();
		dialects.add(new MagnoliaDialect());

		for (String extraDialect : this.extraDialects) {
			try {
				final Class<?> extraDialectClazz = Class.forName(extraDialect);
				final IDialect dialect = (IDialect) extraDialectClazz.newInstance();
				if (logger.isInfoEnabled()) {
					logger.info("Adding Thymeleaf dialect {}", dialect.getClass().getSimpleName());
				}
				dialects.add(dialect);
			} catch (ClassNotFoundException e) {
				logger.error("Cannot find dialect {}", extraDialect);
				throw new IllegalArgumentException(e);
			} catch (IllegalAccessException | InstantiationException e) {
				logger.error("Cannot create Thymeleaf dialect {}: {}", extraDialect, e.getMessage());
				throw new IllegalArgumentException(e);
			}
		}

		return dialects;
	}

	@Override
	protected void onRender(Node content, RenderableDefinition definition, RenderingContext renderingCtx,
			Map<String, Object> ctx, String templateScript) throws RenderException {

		final HttpServletRequest request = MgnlContext.getWebContext().getRequest();
		final HttpServletResponse response = MgnlContext.getWebContext().getResponse();
		final Locale locale = resolveLocale(MgnlContext.getAggregationState().getLocale());

		final Map<String, Object> variables = new HashMap<>(ctx);
		prepareModel(request, response, variables);
		IContext context = createContext(request, response, locale, variables);

		Set<String> selectors = null;

		final String[] templateSpec = templateScript.split("::");
		final String template = templateSpec[0].trim();
		if (templateSpec.length > 1) {
			selectors = new HashSet<>();
			selectors.add(templateSpec[1].trim());
		}

		try (AppendableWriter out = renderingCtx.getAppendable()) {
			final Writer writerWrapper = new AppendableWriterWrapper(out);
			templateEngine.process(template, selectors, context, writerWrapper);
		} catch (IOException ioe) {
			throw new RenderException(ioe);
		}
	}

	protected Locale resolveLocale(Locale locale) {
		if (locale != null) {
			return locale;
		} else if (MgnlContext.hasInstance()) {
			return MgnlContext.getLocale();
		} else {
			return Locale.getDefault();
		}
	}

	protected IContext createContext(HttpServletRequest request, HttpServletResponse response, Locale locale,
			Map<String, Object> variables) {
		final IEngineConfiguration configuration = templateEngine.getConfiguration();
		return new WebExpressionContext(configuration, request, response, servletContext, locale, variables);
	}

	protected void prepareModel(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) {
		// do nothing in the default impl
	}

	public void setExtraDialects(Set<String> extraDialects) {
		this.extraDialects = extraDialects;
		reconfigureDialects();
	}

	public void addExtraDialect(String dialect) {
		this.extraDialects.add(dialect);
		reconfigureDialects();
	}

	private void reconfigureDialects() {
		if (this.templateEngine != null) {
			final Set<IDialect> dialects = createDialects();
			this.templateEngine.setAdditionalDialects(dialects);
		}
	}

	public boolean isCacheTemplates() {
		return cacheTemplates;
	}

	public TemplateEngine getTemplateEngine() {
		return templateEngine;
	}

	public void setTemplateEngine(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}
}
