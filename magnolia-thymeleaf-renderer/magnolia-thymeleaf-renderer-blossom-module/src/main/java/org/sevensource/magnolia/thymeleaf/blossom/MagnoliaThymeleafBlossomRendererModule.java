package org.sevensource.magnolia.thymeleaf.blossom;

/*-
 * #%L
 * Magnolia Thymeleaf Blossom Renderer
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

import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;

/**
 * This class is optional and represents the configuration for the magnolia-thymeleaf-renderer-module module.
 * By exposing simple getter/setter/adder methods, this bean can be configured via content2bean
 * using the properties and node from <tt>config:/modules/magnolia-thymeleaf-renderer-module</tt>.
 * If you don't need this, simply remove the reference to this class in the module descriptor xml.
 */
public class MagnoliaThymeleafBlossomRendererModule implements ModuleLifecycle {

	@Override
	public void start(ModuleLifecycleContext moduleLifecycleContext) {
		
	}

	@Override
	public void stop(ModuleLifecycleContext moduleLifecycleContext) {
		
	}
}
