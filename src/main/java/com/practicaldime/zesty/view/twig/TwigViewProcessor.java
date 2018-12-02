package com.practicaldime.zesty.view.twig;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.resource.reference.ResourceReference;

public class TwigViewProcessor implements ViewProcessor{
    
    protected final ViewConfiguration factory;

    public TwigViewProcessor(ViewConfiguration factory) {
        this.factory = factory;
    }
    
    @Override
    public void write(HttpServletResponse response, JtwigTemplate template, String view, String contentType, Map<String, Object> model) throws IOException {
        response.setContentType(contentType);
        JtwigModel viewModel = JtwigModel.newModel(model);
        template.render(viewModel, new PrintStream(response.getOutputStream(), true, "UTF-8"));
    }

    @Override
    public JtwigTemplate resolve(String templatePath, ResourceReference where) throws Exception {
        ResourceReference resource = new ResourceReference(where.getType(), templatePath);
        JtwigTemplate jtwigTemplate = new JtwigTemplate(factory.getEnvironment(), resource);
        return jtwigTemplate;
    }
}
