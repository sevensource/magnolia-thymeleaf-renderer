package org.sevensource.magnolia.example.config;

/*-
 * #%L
 * magnolia-thymeleaf-example-blossom Magnolia Module
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
