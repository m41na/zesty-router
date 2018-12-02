package com.practicaldime.zesty.view.string;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public class StringViewProcessor implements ViewProcessor{

	@Override
	public void write(HttpServletResponse response, StringTemplate template, String view, String contentType, Map<String, Object> model) throws IOException {
		response.setContentType(contentType);
        //template.process(model, response.getWriter());
	}

	@Override
	public StringTemplate resolve(String templatePath, String where) throws Exception {
		//return config.getTemplate(templatePath);
		return null;
	}
}
