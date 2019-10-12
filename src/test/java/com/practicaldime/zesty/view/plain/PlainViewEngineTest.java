package com.practicaldime.zesty.view.plain;

import com.practicaldime.zesty.view.ViewLookup;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class PlainViewEngineTest {

    private String fileFolder = "/src/test/resources/template/js";
    private String classpathFolder = "/template/js";
    private PlainViewEngine engine;

    @Before
    public void setup() throws IOException {
        engine = PlainViewEngine.create(fileFolder, "NONE");
    }

    @Test
    public void testResolveFileLookup() throws Exception {
        Object path = PlainViewEngine.getProcessor().resolve(fileFolder, "index.test.js", ViewLookup.FILE);
        System.out.printf("*******path resolved: %s%n", path);
    }

    @Test
    public void testMerge() throws Exception {
        try {
            String merged = engine.merge("index.test.js", Collections.emptyMap());
            System.out.printf("*******merged output: %s%n", merged);
        } catch (Exception e) {
            assertEquals("File 'index.test.js' does not exist", e.getMessage());
        }
    }

    @Test
    public void testResolveClasspathLookup() throws Exception {
        Object path = PlainViewEngine.getProcessor().resolve(classpathFolder, "index.test.js", ViewLookup.CLASSPATH);
        System.out.printf("*******path resolved: %s%n", path);
    }

}
