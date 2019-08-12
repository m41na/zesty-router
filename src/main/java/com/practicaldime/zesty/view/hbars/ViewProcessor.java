package com.practicaldime.zesty.view.hbars;

public interface ViewProcessor<T> {

	enum Lookup { FILE, CLASSPATH, REMOTE, ANY };

	T resolve(String templatePath, String template, Lookup strategy) throws Exception;
}
