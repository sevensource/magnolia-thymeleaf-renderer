/*
 * Copyright (c) 2014 Thomas Kratz
 *
 This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Dieses Programm ist Freie Software: Sie können es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Wahl) jeder neueren
    veröffentlichten Version, weiterverbreiten und/oder modifizieren.

    Dieses Programm wird in der Hoffnung, dass es nützlich sein wird, aber
    OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
    Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License für weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */

package org.sevensource.magnolia.thymeleaf.dialect;

import java.util.HashSet;
import java.util.Set;

import org.sevensource.magnolia.thymeleaf.processor.CmsAreaElementProcessor;
import org.sevensource.magnolia.thymeleaf.processor.CmsComponentElementProcessor;
import org.sevensource.magnolia.thymeleaf.processor.CmsInitElementProcessor;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * configures the mgnl processors.
 */
public class MagnoliaDialect extends AbstractProcessorDialect {

	private final static String DIALECT_PREFIX = "cms";

    /**
     * the magnolia dialect.
     */
    public MagnoliaDialect() {
        super(DIALECT_PREFIX, DIALECT_PREFIX, 101);
    }

    /**
     * the magnolia processors.
     * @param dialectName
     * @return
     */
    public Set<IProcessor> getProcessors(String dialectName) {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new CmsInitElementProcessor(dialectName));
        processors.add(new CmsAreaElementProcessor(dialectName));
        processors.add(new CmsComponentElementProcessor(dialectName));
        
        // remove xmlns:cms
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, DIALECT_PREFIX));
        return processors;
    }
}
