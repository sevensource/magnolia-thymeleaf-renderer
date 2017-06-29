package org.sevensource.magnolia.thymeleaf.processor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.sevensource.magnolia.thymeleaf.base.AbstractMockMagnoliaTest;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Created by thomas on 05.03.16.
 */
public class CmsInitTest extends AbstractMockMagnoliaTest {

	ITemplateContext templateContext;
	IEngineConfiguration configuration;
	IModelFactory modelFactory;
	IModel model;
	IElementModelStructureHandler structureHandler;
	
	@Before
	public void before() {
		templateContext = mock(ITemplateContext.class);
		configuration = renderer.getEngine().getConfiguration();
		modelFactory = configuration.getModelFactory(TemplateMode.HTML);
		model = mock(IModel.class);
		structureHandler = mock(IElementModelStructureHandler.class);
		
        when(templateContext.getConfiguration()).thenReturn(configuration);
        when(templateContext.getTemplateMode()).thenReturn(TemplateMode.HTML);
        
        IOpenElementTag tag = modelFactory.createOpenElementTag("head");
        tag = modelFactory.setAttribute(tag,"cms:init", "");
        when(model.get(0)).thenReturn(tag);
	}
    
    @Test
    public void testCmsInitDefault() {
        CmsInitElementProcessor processor = new CmsInitElementProcessor("cms");
        
        processor.doProcess(templateContext, model, structureHandler);

        verify(model).get(0);
        verify(model, times(6)).insert(anyInt(), any());
    }
}
