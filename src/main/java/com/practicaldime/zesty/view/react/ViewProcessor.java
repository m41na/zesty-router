package com.practicaldime.zesty.view.react;

import com.practicaldime.zesty.view.ViewLookup;

public interface ViewProcessor<T> {

	T resolve(String templatePath, String template, ViewLookup strategy) throws Exception;
}
