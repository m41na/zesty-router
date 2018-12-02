package com.practicaldime.zesty.view.twig;

import org.jtwig.environment.Environment;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.environment.EnvironmentFactory;

public class TwigViewConfiguration implements ViewConfiguration{

    protected final Environment environment;
    
    public TwigViewConfiguration() {
        //EnvironmentConfiguration configuration = new DefaultEnvironmentConfiguration();
        EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder
                .configuration()
                .parser()
                .syntax()
                .withStartCode("{%").withEndCode("%}")
                .withStartOutput("${").withEndOutput("}")
                .withStartComment("{#").withEndComment("#}")
                .and()
                .withoutTemplateCache()
                .and()
                .build();

        EnvironmentFactory environmentFactory = new EnvironmentFactory();
        this.environment = environmentFactory.create(configuration);
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }
}
