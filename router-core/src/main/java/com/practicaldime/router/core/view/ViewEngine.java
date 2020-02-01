package com.practicaldime.router.core.view;

import java.util.Map;

public interface ViewEngine {

    String templateDir();

    String templateExt();

    String merge(String template, Map<String, Object> model) throws Exception;
}
