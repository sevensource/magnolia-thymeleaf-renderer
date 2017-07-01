package org.sevensource.magnolia.example.controller.components;

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
