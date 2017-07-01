package org.sevensource.magnolia.example;

import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;
import info.magnolia.module.blossom.module.BlossomModuleSupport;
import org.sevensource.magnolia.example.config.MagnoliaThymeleafBlossomExampleConfiguration;
import org.sevensource.magnolia.example.config.BlossomServletConfiguration;

/**
 * This class manages the lifecycle of the magnolia-thymeleaf-example-blossom module. It starts and stops Spring when Magnolia starts up and
 * shuts down. The dispatcher servlet we create here is a servlet but its managed internally and never exposed to the
 * outside world. A request will never reach this servlet directly. It is only accessed by Magnolia to render the
 * templates, areas and components and display the dialogs managed by the servlet.
 */
public class MagnoliaThymeleafBlossomExample extends BlossomModuleSupport implements ModuleLifecycle {

    public void start(ModuleLifecycleContext moduleLifecycleContext) {
        if (moduleLifecycleContext.getPhase() == ModuleLifecycleContext.PHASE_SYSTEM_STARTUP) {
            super.initRootWebApplicationContext(MagnoliaThymeleafBlossomExampleConfiguration.class);
            super.initBlossomDispatcherServlet("blossom", BlossomServletConfiguration.class);
        }
    }

    public void stop(ModuleLifecycleContext moduleLifecycleContext) {
        if (moduleLifecycleContext.getPhase() == ModuleLifecycleContext.PHASE_SYSTEM_SHUTDOWN) {
            super.destroyDispatcherServlets();
            super.closeRootWebApplicationContext();
        }
    }
}
