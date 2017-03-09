package de.eiswind.magnolia.thymeleaf.processor;

import de.eiswind.magnolia.thymeleaf.base.AbstractMockMagnoliaTest;
import org.junit.Test;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by thomas on 05.03.16.
 */
public class CmsInitTest extends AbstractMockMagnoliaTest {

    @Test
    public void testCmsInit() {
        CmsInitElementProcessor processor = new CmsInitElementProcessor("cms");
        ITemplateContext templateContext = mock(ITemplateContext.class);
        IEngineConfiguration configuration = renderer.getEngine().getConfiguration();
        when(templateContext.getConfiguration()).thenReturn(configuration);
        when(templateContext.getTemplateMode()).thenReturn(TemplateMode.HTML);
        IModelFactory modelFactory = configuration.getModelFactory(TemplateMode.HTML);
        IOpenElementTag tag = modelFactory.createOpenElementTag("head");
        modelFactory.setAttribute(tag,"cms:init", "");
        IModel model = mock(IModel.class);
        // we cannot use the real model because of access restrictions, so this test is pretty limited
        when(model.get(0)).thenReturn(tag);
        IElementModelStructureHandler structureHandler = mock(IElementModelStructureHandler.class);

        processor.doProcess(templateContext, model, structureHandler);

        verify(model).get(0);
        verify(model, times(13)).insert(anyInt(), any());

    }
}
