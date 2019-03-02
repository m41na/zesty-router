package com.practicaldime.zesty.view.string;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultViewProcessor implements ViewProcessor{

	@Override
	public String resolve(String templateDir, String template) throws Exception {
		String baseDir = System.getProperty("user.dir");
		Path path = Paths.get(baseDir, templateDir);
    	return new String(Files.readAllBytes(path.resolve(template)));
	}
}
