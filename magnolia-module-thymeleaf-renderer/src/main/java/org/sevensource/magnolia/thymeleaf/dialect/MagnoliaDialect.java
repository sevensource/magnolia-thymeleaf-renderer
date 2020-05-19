package org.sevensource.magnolia.thymeleaf.dialect;

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

import org.sevensource.magnolia.thymeleaf.processor.CmsAreaElementProcessor;
import org.sevensource.magnolia.thymeleaf.processor.CmsComponentElementProcessor;
import org.sevensource.magnolia.thymeleaf.processor.CmsPageElementProcessor;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashSet;
import java.util.Set;


public class MagnoliaDialect extends AbstractProcessorDialect {

	private static final String DIALECT_PREFIX = "cms";

    public MagnoliaDialect() {
        super(DIALECT_PREFIX, DIALECT_PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
    }

    @Override
	public Set<IProcessor> getProcessors(String dialectName) {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new CmsPageElementProcessor(dialectName));
        processors.add(new CmsAreaElementProcessor(dialectName));
        processors.add(new CmsComponentElementProcessor(dialectName));

        // remove xmlns:cms
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, DIALECT_PREFIX));
        return processors;
    }
}
