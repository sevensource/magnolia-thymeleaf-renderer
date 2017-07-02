package org.sevensource.magnolia.thymeleaf.blossom.configuration;

import javax.inject.Inject;

import org.sevensource.magnolia.thymeleaf.blossom.renderer.BlossomThymeleafRenderer;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import info.magnolia.module.blossom.context.MagnoliaComponentFactoryBean;
import info.magnolia.module.site.renderer.SiteAwareRendererWrapper;
import info.magnolia.registry.RegistrationException;
import info.magnolia.rendering.renderer.Renderer;
import info.magnolia.rendering.renderer.registry.RendererRegistry;


@Configuration
public class SiteAwareThymeleafRenderingConfiguration {

	public final static String SITE_THYMELEAF_BLOSSOM_RENDERER_TYPE = "site-thymeleaf-blossom";
	public final static String THYMELEAF_BLOSSOM_RENDERER_TYPE = "thymeleaf-blossom";

	@Bean
	public MagnoliaComponentFactoryBean rendererRegistry() {
		MagnoliaComponentFactoryBean bf = new MagnoliaComponentFactoryBean();
		bf.setType(RendererRegistry.class);
		return bf;
	}

	@Bean
	@Inject
	public Renderer blossomThymeleafRenderer(ApplicationContext ctx, RendererRegistry registry)
			throws RegistrationException {

		final String rendererType = getThymeleafBlossomRendererType();
		final Renderer renderer = registry.getRenderer(rendererType);

		if (renderer instanceof SiteAwareRendererWrapper) {
			/**
			 * if the renderer is a SiteAwareRendererWrapper, get the underlying
			 * BlossomThymeleafRenderer and apply spring autowiring and
			 * lifecycle methods
			 */
			final String wrappedRendererType = ((SiteAwareRendererWrapper) renderer).getWrappedRendererType();
			final Renderer wrappedRenderer = registry.getRenderer(wrappedRendererType);
			checkRenderer(wrappedRenderer, wrappedRendererType);

			final AutowireCapableBeanFactory bf = ctx.getAutowireCapableBeanFactory();
			bf.autowireBean(wrappedRenderer);
			bf.initializeBean(wrappedRenderer, null);
		} else {
			checkRenderer(renderer, rendererType);
		}

		return renderer;
	}

	protected String getThymeleafBlossomRendererType() {
		return SITE_THYMELEAF_BLOSSOM_RENDERER_TYPE;
	}

	protected void checkRenderer(Renderer renderer, String rendererId) {
		if (!(renderer instanceof BlossomThymeleafRenderer)) {
			final String msg = String.format("Renderer with id %s is of type %s", rendererId,
					renderer.getClass().getName());
			throw new IllegalArgumentException(msg);
		}
	}
}
