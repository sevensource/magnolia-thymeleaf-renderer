package org.sevensource.magnolia.thymeleaf.renderer;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestAttributesPreservingRequestWrapper extends HttpServletRequestWrapper {

	private final Map<String, Object> shadowAttributes = new HashMap<>();

	public RequestAttributesPreservingRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public void setAttribute(String name, Object o) {
		this.shadowAttributes.put(name, o);
	}

	@Override
	public Object getAttribute(String name) {
		if(shadowAttributes.containsKey(name)) {
			return shadowAttributes.get(name);
		} else {
			return super.getAttribute(name);
		}
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		final Set<String> attributeNames = new HashSet<>();
		attributeNames.addAll(Collections.list( super.getAttributeNames() ));
		attributeNames.addAll(shadowAttributes.keySet());
		return Collections.enumeration(attributeNames);
	}

	@Override
	public void removeAttribute(String name) {
		this.shadowAttributes.remove(name);
	}

}
