package org.sevensource.magnolia.thymeleaf.renderer;

import info.magnolia.channel.ChannelResolver;
import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.cms.core.AggregationState;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.decoration.ContentDecoratorUtil;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.wrapper.ChannelVisibilityContentDecorator;
import info.magnolia.jcr.wrapper.I18nNodeWrapper;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.renderer.AbstractRenderer;
import info.magnolia.rendering.template.RenderableDefinition;
import org.apache.commons.lang3.StringUtils;

import javax.jcr.Node;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractThymeleafRenderer extends AbstractRenderer {

	private final ServerConfiguration serverConfiguration;

	public AbstractThymeleafRenderer(RenderingEngine renderingEngine, ServerConfiguration serverConfiguration) {
		super(renderingEngine);
		this.serverConfiguration = serverConfiguration;
	}

	@Override
	protected Map<String, Object> newContext() {
		return new HashMap<>();
	}

	@Override
	protected void setupContext(Map<String, Object> ctx, Node content, RenderableDefinition definition,
								RenderingModel<?> model, Object actionResult) {
		super.setupContext(ctx, content, definition, model, actionResult);

		setContextAttribute(ctx, "ctx", MgnlContext.getInstance());
		setContextAttribute(ctx, "defaultBaseUrl", serverConfiguration.getDefaultBaseUrl());
	}

	@Override
	protected Node wrapNodeForTemplate(Node content) {
		content = wrapWithChannelVisibilityWrapper(content);
		content = wrapWithI18NWrapper(content);
		return content;
	}

	private Node wrapWithI18NWrapper(Node content) {
		if(!NodeUtil.isWrappedWith(content, I18nNodeWrapper.class)) {
			content = new I18nNodeWrapper(content);
		}
		return content;
	}

	private Node wrapWithChannelVisibilityWrapper(Node content) {
		// If it's already wrapped then we don't need to add a new one
		if(ContentDecoratorUtil.isDecoratedWith(content, ChannelVisibilityContentDecorator.class)) {
			return content;
		}
		AggregationState aggregationState = getAggregationStateSafely();
		if(aggregationState == null) {
			return content;
		}
		String channel = aggregationState.getChannel().getName();
		if(StringUtils.isEmpty(channel) || channel.equalsIgnoreCase(ChannelResolver.ALL)) {
			return content;
		}
		return new ChannelVisibilityContentDecorator(channel).wrapNode(content);
	}
}
