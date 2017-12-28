package com.barrybecker4.common.util;

import com.barrybecker4.common.app.ClassLoaderSingleton;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Use to find all classes in a specified package on the classpath.
 * It will find the classes whether they are classes in the project or jar file dependencies.
 * Could be useful in a plugin implementation.
 *
 * @author Barry Becker
 */
public class PackageReflector {

    private static final String CLASS_EXT = ".class";


    /**
     * Finds all classes in the specified package accessible from the class loader.
     *
     * @param packageName the package to search in.
     * @return a list of classes found in the classpath
     */
    public List<Class> getClasses(String packageName)
            throws ClassNotFoundException, IOException {


        List<String> files = getClassNames(packageName);
        return getClassesFromNames(packageName, files);
    }


    private List<String> getClassNames(String packageName) throws IOException {

        String packagePath = packageName.replace('.', '/');

        ClassLoader classLoader = ClassLoaderSingleton.getClassLoader();
        List<String> classNames = new ArrayList<>();
        Enumeration<URL> resources = classLoader.getResources(packagePath);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String dirPath = URLDecoder.decode(resource.getFile(), "UTF-8");

            if (dirPath.startsWith("file:") && dirPath.contains("!")) {
                classNames.addAll(getClassNamesFromJar(dirPath, packageName));
            } else {
                File dir = new File(dirPath);
                List<String> names = getClassNamesFromFiles(Arrays.asList(dir.listFiles()));
                classNames.addAll(names);
            }
        }
        return classNames;
    }

    private List<String> getClassNamesFromJar(String path, String packageName) throws IOException {
        Set<String> classNameSet = new LinkedHashSet<>();
        String[] split = path.split("!");
        URL jar = new URL(split[0]);
        ZipInputStream zip = new ZipInputStream(jar.openStream());
        ZipEntry entry;
        while ((entry = zip.getNextEntry()) != null) {
            if (entry.getName().endsWith(CLASS_EXT)) {
                String className = entry.getName()
                        .replaceAll("[$].*", "")
                        .replaceAll("[.]class", "")
                        .replace('/', '.');

                if (className.startsWith(packageName)) {
                    String name = className.substring(packageName.length() + 1);
                    if (!name.contains(".")) {
                        classNameSet.add(name);
                    }
                }
            }
        }
        return new ArrayList<>(classNameSet);
    }

    private List<String> getClassNamesFromFiles(List<File> files) {
        List<String> classNames = new ArrayList<>();
        for (File file : files) {
            if (file.getName().endsWith(CLASS_EXT)) {
                String className = file.getName().substring(0, file.getName().length() - CLASS_EXT.length());
                classNames.add(className);
            }
        }
        return classNames;
    }

    private ArrayList<Class> getClassesFromNames(String packageName, List<String> classNames)
            throws ClassNotFoundException {

        ArrayList<Class> classes = new ArrayList<>();
        for (String className : classNames) {
            classes.add(Class.forName(packageName + '.' + className));
        }
        return classes;
    }
}