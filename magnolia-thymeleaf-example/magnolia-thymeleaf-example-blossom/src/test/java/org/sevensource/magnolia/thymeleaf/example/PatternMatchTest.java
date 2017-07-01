package org.sevensource.magnolia.thymeleaf.example;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.util.PatternMatchUtils;

public class PatternMatchTest {

	private static void assertViews(String[] viewNames, String viewName, boolean expected) {
		boolean result = PatternMatchUtils.simpleMatch(viewNames, viewName);
		assertThat(result, is(expected));
	}
	
	private static void assertViews(String[] viewNames, String viewName) {
		assertViews(viewNames, viewName, true);
	}
	
	
	@Test
	public void simple() {
		assertViews(new String[]{"*.html"}, "/home/test.html");
	}
	
	@Test
	public void fragment() {
		assertViews(new String[]{"*.html", "*.html*::*"}, "/home/test.html :: main");
		assertViews(new String[]{"*.html", "*.html*::*"}, "/home/test.html:: main");
		assertViews(new String[]{"*.html", "*.html*::*"}, "/home/test.html::main");
	}
}
