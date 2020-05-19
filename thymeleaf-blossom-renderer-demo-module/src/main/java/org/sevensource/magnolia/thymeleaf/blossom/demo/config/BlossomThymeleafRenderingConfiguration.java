package org.sevensource.magnolia.thymeleaf.blossom.demo.config;

import info.magnolia.module.blossom.view.TemplateViewResolver;
import info.magnolia.rendering.renderer.Renderer;
import org.sevensource.magnolia.thymeleaf.blossom.configuration.SiteAwareThymeleafRenderingConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;


@Configuration
@Import({SiteAwareThymeleafRenderingConfiguration.class})
public class BlossomThymeleafRenderingConfiguration {

	@Bean
	@Inject
	public TemplateViewResolver thymeleafTemplateViewResolver(Renderer viewRenderer) throws Exception {
		TemplateViewResolver resolver = new TemplateViewResolver();
		resolver.setOrder(3);
		resolver.setPrefix("thymeleaf-blossom-renderer-demo/");
		resolver.setViewNames("*.html*::*", "*.html");
		resolver.setViewRenderer(viewRenderer);
		// development
		resolver.setCache(false);
		return resolver;
	}
}
