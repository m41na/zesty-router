package com.practicaldime.router.view.twig;

import com.practicaldime.router.core.view.ViewProcessor;
import org.jtwig.JtwigTemplate;
import org.jtwig.resource.reference.ResourceReference;

public class JTwigViewProcessor implements ViewProcessor<JtwigTemplate, ResourceReference> {

    protected final ViewConfiguration factory;

    public JTwigViewProcessor(ViewConfiguration factory) {
        this.factory = factory;
    }

    @Override
    public JtwigTemplate resolve(String templatePath, String template, ResourceReference lookup) throws Exception {
        ResourceReference resource = new ResourceReference(lookup.getType(), templatePath);
        JtwigTemplate jtwigTemplate = new JtwigTemplate(factory.getEnvironment(), resource);
        return jtwigTemplate;
    }
}
