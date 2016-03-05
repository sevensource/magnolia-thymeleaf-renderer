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

import static org.mockito.Mockito.*;

/**
 * Created by thomas on 05.03.16.
 */
public class CmsInitTest extends AbstractMockMagnoliaTest{

    @Test
    public void testCmsInit(){
        CmsInitElementProcessor processor = new CmsInitElementProcessor();
        ITemplateContext templateContext = mock(ITemplateContext.class);
        IEngineConfiguration configuration = renderer.getEngine().getConfiguration();
        when(templateContext.getConfiguration()).thenReturn(configuration);
        when(templateContext.getTemplateMode()).thenReturn(TemplateMode.HTML);
        IModelFactory modelFactory =configuration.getModelFactory(TemplateMode.HTML);
        IOpenElementTag tag = modelFactory.createOpenElementTag("head");
        tag.getAttributes().setAttribute("cms:init","");

        IModel model =mock(IModel.class);
        when(model.get(0)).thenReturn(tag);
        IElementModelStructureHandler structureHandler = mock(IElementModelStructureHandler.class);
        processor.doProcess(templateContext,model,structureHandler);

        verify(model).get(0);
        verify(model, times(13)).insert(anyInt(),any());

    }
}
