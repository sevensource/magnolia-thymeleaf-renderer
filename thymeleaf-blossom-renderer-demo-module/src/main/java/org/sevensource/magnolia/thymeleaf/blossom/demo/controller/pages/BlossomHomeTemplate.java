package org.sevensource.magnolia.thymeleaf.blossom.demo.controller.pages;

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

import info.magnolia.module.blossom.annotation.Area;
import info.magnolia.module.blossom.annotation.AvailableComponentClasses;
import info.magnolia.module.blossom.annotation.Inherits;
import info.magnolia.module.blossom.annotation.TabFactory;
import info.magnolia.module.blossom.annotation.Template;
import info.magnolia.ui.form.config.TabBuilder;
import info.magnolia.ui.framework.config.UiConfig;
import org.sevensource.magnolia.thymeleaf.blossom.demo.controller.components.BlossomTeaserComponent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;

@Template(id="thymeleaf-blossom-renderer-demo:pages/blossomHome", title="Blossom Th Home")
@Controller
public class BlossomHomeTemplate {

    @TabFactory("main")
    public void mainTab(UiConfig cfg, TabBuilder tab) {
        tab.fields(
                cfg.fields.text("title").label("Title"),
                cfg.fields.date("date").label("Date")
        );
    }

	@RequestMapping("/pages/blossomHome")
	public String blossomHome(Model model) {
		model.addAttribute("currentDate", Instant.now());
		return "pages/blossom-home.html";
	}

	@Area("mainContent")
	@Inherits
    @Controller
	@AvailableComponentClasses({BlossomTeaserComponent.class})
    public static class MainContentArea {
        @RequestMapping("/pages/blossomHome/mainContent")
        public String render() {
        	return "areas/areas.html :: mainContent";
        }
    }
}
