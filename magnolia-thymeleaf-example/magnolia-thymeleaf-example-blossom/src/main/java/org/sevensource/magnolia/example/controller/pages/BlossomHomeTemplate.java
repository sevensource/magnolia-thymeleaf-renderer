package org.sevensource.magnolia.example.controller.pages;

import java.util.GregorianCalendar;

import org.sevensource.magnolia.example.controller.components.BlossomTeaserComponent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import info.magnolia.module.blossom.annotation.Area;
import info.magnolia.module.blossom.annotation.AvailableComponentClasses;
import info.magnolia.module.blossom.annotation.AvailableComponents;
import info.magnolia.module.blossom.annotation.Inherits;
import info.magnolia.module.blossom.annotation.TabFactory;
import info.magnolia.module.blossom.annotation.Template;
import info.magnolia.ui.form.config.TabBuilder;
import info.magnolia.ui.framework.config.UiConfig;

@Template(id="magnolia-thymeleaf-example-blossom:pages/blossomHome", title="Blossom Th Home")
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
		model.addAttribute("currentDate", new GregorianCalendar());
		return "pages/blossom-home.html";
	}
	
	@Area("mainContent")
	@Inherits
    @Controller
    @AvailableComponents({"magnolia-thymeleaf-example-renderer:components/th-teaser"})
	@AvailableComponentClasses({BlossomTeaserComponent.class})
    public static class MainContentArea {
        @RequestMapping("/pages/blossomHome/mainContent")
        public String render() {
        	return "areas/areas.html :: mainContent";
        }
    }
}
