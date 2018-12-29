package com.practicaldime.zesty.view.string;

public interface ViewProcessor {

	public String resolve(String templatePath, String where) throws Exception;
}
