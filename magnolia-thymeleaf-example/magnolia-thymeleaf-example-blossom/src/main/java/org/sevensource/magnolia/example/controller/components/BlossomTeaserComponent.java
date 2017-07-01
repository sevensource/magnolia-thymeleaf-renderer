package org.sevensource.magnolia.example.controller.components;

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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import info.magnolia.module.blossom.annotation.TabFactory;
import info.magnolia.module.blossom.annotation.Template;
import info.magnolia.ui.form.config.TabBuilder;
import info.magnolia.ui.framework.config.UiConfig;

@Template(id="magnolia-thymeleaf-example-blossom:components/blossomTeaser", title="Blossom Th Teaser")
@Controller
public class BlossomTeaserComponent {
	
    @TabFactory("main")
    public void mainTab(UiConfig cfg, TabBuilder tab) {
        tab.fields(
                cfg.fields.text("title").label("Title"),
                cfg.fields.link("href").appName("pages").label("Link")
        );
    }
    
	@RequestMapping("/components/blossomTeaser")
	public String render() {
		return "components/blossom-teaser.html";
	}
}
