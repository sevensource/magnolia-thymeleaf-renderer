package org.sevensource.magnolia.thymeleaf.renderer;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;

import info.magnolia.cms.core.AggregationState;
import info.magnolia.jcr.decoration.ContentDecoratorUtil;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.wrapper.ChannelVisibilityContentDecorator;
import info.magnolia.jcr.wrapper.I18nNodeWrapper;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.renderer.AbstractRenderer;

public abstract class AbstractThymeleafRenderer extends AbstractRenderer {
	
	public AbstractThymeleafRenderer(RenderingEngine renderingEngine) {
		super(renderingEngine);
	}
	
    @Override
    protected Map<String, Object> newContext() {
        return new HashMap<>();
    }
    
    @Override
    protected Node wrapNodeForTemplate(Node content) {
        content = wrapWithChannelVisibilityWrapper(content);
        content = wrapWithI18NWrapper(content);
        return content;
    }

    private Node wrapWithI18NWrapper(Node content) {
        if (!NodeUtil.isWrappedWith(content, I18nNodeWrapper.class)) {
            content = new I18nNodeWrapper(content);
        }
        return content;
    }

    private Node wrapWithChannelVisibilityWrapper(Node content) {
        // If it's already wrapped then we don't need to add a new one
        if (ContentDecoratorUtil.isDecoratedWith(content, ChannelVisibilityContentDecorator.class)) {
            return content;
        }
        AggregationState aggregationState = getAggregationStateSafely();
        if (aggregationState == null) {
            return content;
        }
        String channel = aggregationState.getChannel().getName();
        if (StringUtils.isEmpty(channel) || channel.equalsIgnoreCase("all")) {
            return content;
        }
        return new ChannelVisibilityContentDecorator(channel).wrapNode(content);
    }
}
