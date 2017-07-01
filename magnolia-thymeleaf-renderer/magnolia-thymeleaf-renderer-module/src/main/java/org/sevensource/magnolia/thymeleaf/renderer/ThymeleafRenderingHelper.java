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

import java.io.Writer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;

import info.magnolia.context.MgnlContext;
import info.magnolia.rendering.engine.RenderException;

public class ThymeleafRenderingHelper {

	private static final Logger logger = LoggerFactory.getLogger(ThymeleafRenderingHelper.class);
	
	private ITemplateEngine templateEngine;
	
	public ThymeleafRenderingHelper(ITemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}
	
	public void render(HttpServletRequest request, HttpServletResponse response, String templateScript, Locale locale, Map<String, Object> variables, Writer out) throws RenderException {
		
		IContext context = createContext(request, response, locale, variables);
		
        Set<String> selectors = null;
        
        final String[] templateSpec = templateScript.split("::");
        final String template = templateSpec[0].trim();
        if(templateSpec.length > 1) {
        	selectors = new HashSet<>();
        	selectors.add(templateSpec[1].trim());
        }
        
        templateEngine.process(template, selectors, context, out);
	}
    
    protected IContext createContext(HttpServletRequest request, HttpServletResponse response, Locale locale, Map<String, Object> variables) {
        final Locale localeToUse = checkLocale(locale);
        addDefaultData(variables);
        
        final ServletContext servletContext = request.getServletContext();

        return new WebContext(request, response, servletContext, localeToUse, variables);
    }
    
    protected Locale checkLocale(Locale locale) {
        if (locale != null) {
            return locale;
        } else if (MgnlContext.hasInstance()) {
            return MgnlContext.getLocale();
        } else {
            return Locale.getDefault();
        }
    }

    protected void addDefaultData(Map<String, Object> variables) {
    	// Nothing in the default IMPL
    }
}
