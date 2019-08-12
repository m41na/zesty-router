package com.practicaldime.zesty.view.react;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.zesty.demo.CommentsRepo;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Ignore("fix later")
public class ReactViewEngineTest {

	private String fileFolder = "src/test/resources/template/js";
	private String classpathFolder = "/template/js";
	private String testFile = "comments-list.test.js";
	private ReactViewEngine engine;
	
	@Before
	public void setup() throws IOException {
		engine = ReactViewEngine.create(fileFolder, "js");
	}
	
	@Test
	public void testResolveFileLookup() throws Exception {
		Object path = ReactViewEngine.getProcessor().resolve(fileFolder, testFile, ViewProcessor.Lookup.FILE);
		System.out.printf("*******path resolved: %s%n", path);
	}

	@Test
	public void testMerge() throws Exception {
		Map<String, Object> model = new HashMap<>();
		model.put("comments", CommentsRepo.comments());
		String merged = engine.merge(testFile, model);
		System.out.printf("*******merged output: %s%n", merged);
	}
	
	@Test
	public void testResolveClasspathLookup() throws Exception {
		Object path = ReactViewEngine.getProcessor().resolve(classpathFolder, testFile, ViewProcessor.Lookup.CLASSPATH);
		System.out.printf("*******path resolved: %s%n", path);
	}
}
