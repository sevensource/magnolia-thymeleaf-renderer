package org.sevensource.magnolia.thymeleaf.dialect;

import java.util.HashSet;
import java.util.Set;

import org.sevensource.magnolia.thymeleaf.processor.CmsAreaElementProcessor;
import org.sevensource.magnolia.thymeleaf.processor.CmsComponentElementProcessor;
import org.sevensource.magnolia.thymeleaf.processor.CmsPageElementProcessor;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;


public class MagnoliaDialect extends AbstractProcessorDialect {

	private final static String DIALECT_PREFIX = "cms";
	
    public MagnoliaDialect() {
        super(DIALECT_PREFIX, DIALECT_PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
    }

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
