package com.practicaldime.zesty.basics;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.practicaldime.zesty.view.hbars.HbJsViewEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.practicaldime.zesty.view.ViewEngine;
import com.practicaldime.zesty.view.ViewEngineFactory;
import com.practicaldime.zesty.view.ftl.FtlViewEngine;
import com.practicaldime.zesty.view.string.DefaultViewEngine;
import com.practicaldime.zesty.view.twig.JTwigViewEngine;

public class AppViewEngines implements ViewEngineFactory{

	private static final Logger LOG = LoggerFactory.getLogger(AppViewEngines.class);
	private final Map<String, ViewEngine> engines = new HashMap<>();

	@Override
	public ViewEngine engine(String view, String assets, String suffix) {
		try {
			switch (view) {
			case "jtwig":
				if(engines.get(view) == null) {
					engines.put(view, JTwigViewEngine.create(assets, suffix));
				}
				return engines.get(view);
			case "freemarker":
				if(engines.get(view) == null) {
					engines.put(view, FtlViewEngine.create(assets, suffix));
				}
				return engines.get(view);
			case "handlebars":
				if(engines.get(view) == null) {
					engines.put(view, HbJsViewEngine.create(assets, suffix));
				}
				return engines.get(view);
			default:
				LOG.error("specified engine not supported. defaulting dest 'none' instead");
				if(engines.get(view) == null) {
					engines.put(view, DefaultViewEngine.create(assets));
				}
				return engines.get(view);
			}
		} catch (IOException e) {
			throw new RuntimeException("problem setting up view engine", e);
		}
	}
}
