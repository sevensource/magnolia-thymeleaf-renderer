package org.sevensource.magnolia.thymeleaf.blossom.demo.config;

/*-
 * #%L
 * thymeleaf-blossom-renderer-demo Magnolia Module
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

import info.magnolia.cms.beans.config.VirtualURIMapping;
import info.magnolia.module.blossom.annotation.Area;
import info.magnolia.module.blossom.annotation.DialogFactory;
import info.magnolia.module.blossom.annotation.Template;
import info.magnolia.module.blossom.annotation.VirtualURIMapper;
import info.magnolia.module.blossom.preexecution.BlossomHandlerMapping;
import info.magnolia.module.blossom.view.UuidRedirectViewResolver;
import info.magnolia.module.blossom.web.BlossomHandlerMethodArgumentResolver;
import info.magnolia.module.blossom.web.BlossomRequestMappingHandlerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Collections;

/**
 * Configuration class for the blossom servlet housing templates and beans used to render them.
 */
@Configuration
@ComponentScan(
		basePackages = {"org.sevensource.magnolia.thymeleaf.blossom.demo"},
		includeFilters = {
				@ComponentScan.Filter(Template.class),
				@ComponentScan.Filter(Area.class),
				@ComponentScan.Filter(DialogFactory.class),
				@ComponentScan.Filter(VirtualURIMapper.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = VirtualURIMapping.class)
		})
@Import({BlossomThymeleafRenderingConfiguration.class})
public class BlossomServletConfiguration {

	/**
	 * Handler adapter for &#64;RequestMapping style handler methods. Uses BlossomRequestMappingHandlerAdapter for
	 * support of flash attributes in uuid redirect views.
	 */
	@Bean
	public HandlerAdapter handlerAdapter(BlossomHandlerMethodArgumentResolver blossomHandlerMethodArgumentResolver) {

		BlossomRequestMappingHandlerAdapter handlerAdapter = new BlossomRequestMappingHandlerAdapter();
		handlerAdapter.setRedirectPatterns("website:*");

		handlerAdapter.setCustomArgumentResolvers(Collections.singletonList(blossomHandlerMethodArgumentResolver));

		// For @Valid - JSR-303 Bean Validation API -->
		ConfigurableWebBindingInitializer bindingInitializer = new ConfigurableWebBindingInitializer();
		bindingInitializer.setValidator(validatorFactory());
		handlerAdapter.setWebBindingInitializer(bindingInitializer);

		return handlerAdapter;
	}

	@Bean
	public BlossomHandlerMethodArgumentResolver blossomHandlerMethodArgumentResolver() {
		return new BlossomHandlerMethodArgumentResolver();
	}

	@Bean
	public LocalValidatorFactoryBean validatorFactory() {
		return new LocalValidatorFactoryBean();
	}

	/**
	 * Handler adapter for Controller interface style controllers. Required because these are used internally by the
	 * pre-execution mechanism.
	 */
	@Bean
	public HandlerAdapter controllerHandlerAdapter() {
		return new SimpleControllerHandlerAdapter();
	}

	/**
	 * Handler mapping that delegates to other handler mappings. Required by the pre-execution mechanism and must be
	 * ordered first.
	 */
	@Bean
	public HandlerMapping blossomHandlerMapping() {
		BlossomHandlerMapping handlerMapping = new BlossomHandlerMapping();
		handlerMapping.setOrder(1);
		handlerMapping.setTargetHandlerMappings(new HandlerMapping[]{mappingHandlerMapping()});
		return handlerMapping;
	}

	/**
	 * Handler mapping for &#64;RequestMapping style handler methods.
	 */
	@Bean
	public RequestMappingHandlerMapping mappingHandlerMapping() {
		RequestMappingHandlerMapping handlerMapping = new RequestMappingHandlerMapping();
		handlerMapping.setOrder(2);
		handlerMapping.setUseSuffixPatternMatch(false);
		return handlerMapping;
	}

	/**
	 * View resolver for uuid redirect views.
	 */
	@Bean
	public UuidRedirectViewResolver uuidRedirectViewResolver() {
		UuidRedirectViewResolver resolver = new UuidRedirectViewResolver();
		resolver.setOrder(1);
		return resolver;
	}
}
