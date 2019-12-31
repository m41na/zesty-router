package com.practicaldime.zesty.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * Expects to find a 'deploy' folder in the current location that contains exactly one jar file.
 * Expects the loaded jar file to contain a manifest, and a Main-Class attribute in that manifest
 */
public class AppLoader {

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        File folder = Paths.get(".", "deploy").toFile();
        if (folder.isDirectory()) {
            File[] files = folder.listFiles(pathname -> pathname.isFile() && pathname.getPath().endsWith(".jar"));
            if (files.length == 0) {
                throw new RuntimeException("Expected a jar file");
            }
            if (files.length > 1) {
                throw new RuntimeException("Expected just one jar file");
            }

            File jar = files[0];

            //load jar
            URLClassLoader child = new URLClassLoader(
                    new URL[]{jar.toURI().toURL()},
                    AppLoader.class.getClassLoader()
            );

            JarInputStream jarStream = new JarInputStream(new FileInputStream(jar));
            Manifest manifest = jarStream.getManifest();
            Attributes attr = manifest.getMainAttributes();

            Class mainClass = Class.forName(attr.getValue("Main-Class"), true, child);
            Method main = mainClass.getMethod("main", args.getClass());
            int mods = main.getModifiers();
            if (main.getReturnType() != void.class || !Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
                throw new NoSuchMethodException("main");
            }
            try {
                //invoke main method
                main.invoke(null, new Object[]{args});
            } catch (IllegalAccessException e) {
                // This should not happen, as we have disabled access checks
            }
            System.out.println("application loaded successfully");
        } else {
            System.err.println("Expecting a 'deploy' folder with an application jar");
        }
    }
}
