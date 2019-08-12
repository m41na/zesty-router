package com.practicaldime.zesty.view.twig;

import org.jtwig.JtwigTemplate;
import org.jtwig.resource.reference.ResourceReference;

public class JTwigViewProcessor implements ViewProcessor{
    
    protected final ViewConfiguration factory;

    public JTwigViewProcessor(ViewConfiguration factory) {
        this.factory = factory;
    }    

    @Override
    public JtwigTemplate resolve(String templatePath, ResourceReference where) throws Exception {
        ResourceReference resource = new ResourceReference(where.getType(), templatePath);
        JtwigTemplate jtwigTemplate = new JtwigTemplate(factory.getEnvironment(), resource);
        return jtwigTemplate;
    }
}
