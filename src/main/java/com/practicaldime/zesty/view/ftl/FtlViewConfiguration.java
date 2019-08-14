package com.practicaldime.zesty.view.ftl;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Version;

import java.io.File;
import java.io.IOException;

public class FtlViewConfiguration implements ViewConfiguration {

    // Where the application is initialized; in general you do this ONLY ONCE in the application life-cycle!
    protected final Configuration cfg;

    public FtlViewConfiguration(String assets) throws IOException {
    	String path = assets != null && assets.trim().length() > 0? assets.trim() : "./";
        cfg = new Configuration(new Version(2, 3, 23));
        TemplateLoader[] loaders = new TemplateLoader[]{
            new FileTemplateLoader(new File(path)),
            new ClassTemplateLoader(FtlViewConfiguration.class, path)
        };
        cfg.setTemplateLoader(new MultiTemplateLoader(loaders));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setDateTimeFormat("dd MMM, yy 'at' hh:mm:ssa");
        cfg.setNumberFormat("computer");
        //cfg.setTagSyntax(Configuration.ANGLE_BRACKET_TAG_SYNTAX);
        //BeansWrapperBuilder builder = new BeansWrapperBuilder(Configuration.VERSION_2_3_23);
        //builder.setExposeFields(true);
        //cfg.setObjectWrapper(builder.build());
    }

    @Override
    public Configuration getEnvironment() {
        return this.cfg;
    }
}
