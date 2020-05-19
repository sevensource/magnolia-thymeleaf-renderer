//package org.sevensource.magnolia.thymeleaf.example;
//
///*-
// * #%L
// * thymeleaf-blossom-renderer-demo Magnolia Module
// * %%
// * Copyright (C) 2017 SevenSource, pgaschuetz
// * %%
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as
// * published by the Free Software Foundation, either version 3 of the
// * License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public
// * License along with this program.  If not, see
// * <http://www.gnu.org/licenses/gpl-3.0.html>.
// * #L%
// */
//
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.assertThat;
//
//import org.junit.Test;
//import org.springframework.util.PatternMatchUtils;
//
//public class PatternMatchTest {
//
//	private static void assertViews(String[] viewNames, String viewName, boolean expected) {
//		boolean result = PatternMatchUtils.simpleMatch(viewNames, viewName);
//		assertThat(result, is(expected));
//	}
//
//	private static void assertViews(String[] viewNames, String viewName) {
//		assertViews(viewNames, viewName, true);
//	}
//
//
//	@Test
//	public void simple() {
//		assertViews(new String[]{"*.html"}, "/home/test.html");
//	}
//
//	@Test
//	public void fragment() {
//		assertViews(new String[]{"*.html", "*.html*::*"}, "/home/test.html :: main");
//		assertViews(new String[]{"*.html", "*.html*::*"}, "/home/test.html:: main");
//		assertViews(new String[]{"*.html", "*.html*::*"}, "/home/test.html::main");
//	}
//}
