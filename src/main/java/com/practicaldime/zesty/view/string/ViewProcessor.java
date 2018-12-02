package com.practicaldime.zesty.view.string;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public interface ViewProcessor {

	public void write(HttpServletResponse response, StringTemplate template, String view, String contentType, Map<String, Object> model) throws IOException;

	public StringTemplate resolve(String templatePath, String where) throws Exception;
}
