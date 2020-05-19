package org.sevensource.magnolia.thymeleaf.setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;
import info.magnolia.rendering.module.setup.InstallRendererContextAttributeTask;

public class MagnoliaThymeleafRendererVersionHandler extends DefaultModuleVersionHandler {

	private static final Map<String, String> ctxAttributes = new HashMap<>();
	static {
		ctxAttributes.put("damfn",	"info.magnolia.dam.templating.functions.DamTemplatingFunctions");
		ctxAttributes.put("imgfn",	"info.magnolia.imaging.functions.ImagingTemplatingFunctions");
		ctxAttributes.put("resfn",	"info.magnolia.modules.resources.templating.ResourcesTemplatingFunctions");
		ctxAttributes.put("sitefn",	"info.magnolia.module.site.functions.SiteFunctions");
		ctxAttributes.put("navfn",	"info.magnolia.templating.functions.NavigationTemplatingFunctions");
		ctxAttributes.put("searchfn","info.magnolia.templating.functions.SearchTemplatingFunctions");
		ctxAttributes.put("cmsfn",	"info.magnolia.templating.functions.TemplatingFunctions");
	}

	@Override
	protected List<Task> getExtraInstallTasks(InstallContext installContext) {
		List<Task> tasks = new ArrayList<>(super.getExtraInstallTasks(installContext));

        for(Entry<String, String> entry : ctxAttributes.entrySet()) {
        	try {
        		 final Class<?> ctxClazz = Class.forName( entry.getValue() );
        		 final Task task = new InstallRendererContextAttributeTask(
        				 "rendering", "thymeleaf", entry.getKey(), ctxClazz.getName());
        	     tasks.add(task);
        		} catch( ClassNotFoundException e ) {
        			// don't do nothing...
        		}
        }

        return tasks;
	}
}
