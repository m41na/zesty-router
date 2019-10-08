package com.practicaldime.zesty.view.hbjs;

import com.practicaldime.zesty.view.ViewLookup;
import com.practicaldime.zesty.view.hbars.HbJsViewEngine;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HbJsViewEngineTest {

	private String fileFolder = "/src/test/resources/template/js";
	private String classpathFolder = "/template/js";
	private String testFile = "handlebars.test.js";
	private HbJsViewEngine engine;
	
	@Before
	public void setup() throws IOException {
		engine = HbJsViewEngine.create(fileFolder, "js", "FILE");
	}
	
	@Test
	public void testResolveFileLookup() throws Exception {
		Object path = HbJsViewEngine.getProcessor().resolve(fileFolder, testFile, ViewLookup.FILE);
		System.out.printf("*******path resolved: %s%n", path);
	}

	@Test
	public void testMerge() throws Exception {
		Map<String, Object> model = new HashMap<>();
		model.put("numbers", new int[]{1,2,3,4,5,6});
		String merged = engine.merge(testFile, model);
		System.out.printf("*******merged output: %s%n", merged);
	}
	
	@Test
	public void testResolveClasspathLookup() throws Exception {
		Object path = HbJsViewEngine.getProcessor().resolve(classpathFolder, testFile, ViewLookup.CLASSPATH);
		System.out.printf("*******path resolved: %s%n", path);
	}

}
