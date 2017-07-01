package org.sevensource.magnolia.example.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import info.magnolia.module.blossom.context.ConfiguredBeanFactoryBean;
import info.magnolia.module.blossom.view.TemplateViewResolver;
import info.magnolia.module.site.renderer.SiteAwareRendererWrapper;
import info.magnolia.rendering.renderer.Renderer;

/**
 * Configuration class for using Thymeleaf views and merge in configuration from the site definition template prototype.
 */
@Configuration
public class SiteAwareThymeleafRenderingConfiguration {
	
	
//	@SuppressWarnings("unchecked")
//	@Bean
//	public FactoryBean<SiteAwareRendererWrapper> thymeleafViewRenderer() {
//		ConfiguredBeanFactoryBean bf = new ConfiguredBeanFactoryBean();
//		bf.setPath("/modules/site/renderers/site-thymeleaf");
//		bf.setDefaultClass(SiteAwareRendererWrapper.class);
//		return bf;
//	}
	
	@SuppressWarnings("unchecked")
	@Bean
	public FactoryBean<Renderer> thymeleafViewRenderer() {
		ConfiguredBeanFactoryBean bf = new ConfiguredBeanFactoryBean();
		bf.setPath("/modules/rendering/renderers/thymeleaf-blossom");
		return bf;
	}
	
  @Bean
  public TemplateViewResolver thymeleafTemplateViewResolver() throws Exception {
	  
	  final Renderer viewRenderer = thymeleafViewRenderer().getObject();
	  
      TemplateViewResolver resolver = new TemplateViewResolver();
      resolver.setOrder(3);
      resolver.setPrefix("magnolia-thymeleaf-example-blossom/");
      resolver.setViewNames("*.html*::*", "*.html");
      resolver.setViewRenderer(viewRenderer);
      return resolver;
  }
}
