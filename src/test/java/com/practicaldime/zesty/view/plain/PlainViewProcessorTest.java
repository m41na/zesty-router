package com.practicaldime.zesty.view.plain;

import com.practicaldime.zesty.view.ViewLookup;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlainViewProcessorTest {

    private PlainViewProcessor view = new PlainViewProcessor();

    @Test
    public void resolveWithFileLookup() throws Exception {
        try {
            String template = view.resolve("www", "hello", ViewLookup.FILE);
            System.out.println(template);
            fail("expected failure since 'hello' does not exist");
        }
        catch(Exception e){
            String message = e.getMessage();
            assertEquals("Expected 'File 'hello' does not exist'", "File 'hello' does not exist", message);
        }

        String template = view.resolve("/src/test/resources/template/js", "ejs.test.js", ViewLookup.FILE);
        System.out.println(template);
        assertTrue("Expected '<% comments.comments.forEach(function(comment){ %>' in the content", template.contains("<% comments.comments.forEach(function(comment){ %>") );
    }

    @Test
    public void resolveWithClasspathLookup() throws Exception {
        try {
            String template = view.resolve("www", "hello", ViewLookup.CLASSPATH);
            System.out.println(template);
            fail("expected failure since 'hello' does not exist");
        }
        catch(Exception e){
            String message = e.getMessage();
            assertEquals("Expected 'Resource 'hello' does not exist'", "Resource 'hello' does not exist", message);
        }

        String template = view.resolve("template/js", "ejs.test.js", ViewLookup.CLASSPATH);
        System.out.println(template);
        assertTrue("Expected '<% comments.comments.forEach(function(comment){ %>' in the content", template.contains("<% comments.comments.forEach(function(comment){ %>") );
    }

    @Test
    public void resolveWithNoneLookup() throws Exception {
        String template = view.resolve("www", "hello", ViewLookup.NONE);
        System.out.println(template);
        assertNull("Expected 'null'", template);
    }

    @Test
    public void resolveWithAnyLookup() throws Exception {
        String template = view.resolve("www", "hello", ViewLookup.ANY);
        System.out.println(template);
        assertNull("Expected 'null'", template);
    }
}