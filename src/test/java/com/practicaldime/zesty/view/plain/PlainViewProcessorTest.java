package com.practicaldime.zesty.view.plain;

import com.practicaldime.zesty.view.ViewLookup;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlainViewProcessorTest {

    PlainViewProcessor view = new PlainViewProcessor();

    @Test
    public void resolveWithFileLookup() throws Exception {
        String template = view.resolve("www", "hello", ViewLookup.FILE);
        System.out.println(template);
        assertEquals("Expected 'File 'hello' does not exist'", "File 'hello' does not exist", template);

        template = view.resolve("src/test/resources/template/js", "ejs.test.js", ViewLookup.FILE);
        System.out.println(template);
        assertTrue("Expected '<% comments.comments.forEach(function(comment){ %>' in the content", template.contains("<% comments.comments.forEach(function(comment){ %>") );
    }

    @Test
    public void resolveWithClasspathLookup() throws Exception {
        String template = view.resolve("www", "hello", ViewLookup.CLASSPATH);
        System.out.println(template);
        assertEquals("Expected 'Resource 'hello' does not exist'", "Resource 'hello' does not exist", template);

        template = view.resolve("template/js", "ejs.test.js", ViewLookup.CLASSPATH);
        System.out.println(template);
        assertTrue("Expected '<% comments.comments.forEach(function(comment){ %>' in the content", template.contains("<% comments.comments.forEach(function(comment){ %>") );
    }

    @Test
    public void resolveWithNoneLookup() throws Exception {
        String template = view.resolve("www", "hello", ViewLookup.NONE);
        System.out.println(template);
        assertEquals("Expected 'hello'", "hello", template);
    }

    @Test
    public void resolveWithAnyLookup() throws Exception {
        String template = view.resolve("www", "hello", ViewLookup.ANY);
        System.out.println(template);
        assertEquals("Expected 'not yet implemented'", "not yet implemented", template);
    }
}