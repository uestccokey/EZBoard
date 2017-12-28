/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.util;

import com.barrybecker4.common.app.ClassLoaderSingleton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;

/**
 * Miscellaneous commonly used file related static utility methods.
 *
 * @author Barry Becker
 */
public final class FileUtil {

    /**
     * Get the correct file separator whether on windows (\) or linux (/).
     * Getting error in applets if trying to use System.getProperty("file.separator")
     */
    public static final String FILE_SEPARATOR = "/";


    /**
     * cannot instantiate static class.
     */
    private FileUtil() {}

    /**
     * Try not to use this.
     * If this is called from an applet, it will give a security exception.
     *
     * @return home directory. Assumes running as an Application.
     */
    public static String getHomeDir() {
        String home = FILE_SEPARATOR;
        try {
            home = System.getProperty("user.dir") + FILE_SEPARATOR;
        } catch (AccessControlException e) {
            System.out.println("You do not have access to user.dir. This can happen when running as an applet. ");
        }
        return home;
    }

    /**
     * Tries to create the specified directory if it does not exist.
     *
     * @param path path to the directory to verify
     * @throws IOException if any problem creating the specified directory
     */
    public static void verifyDirectoryExistence(String path) throws IOException {
        File directory = new File(path);

        if (!directory.exists()) {
            boolean success = directory.mkdir();
            if (!success) {
                throw new IOException("Could not create directory: " + directory.getAbsolutePath());
            }
        }
    }

    /**
     * create a PrintWriter with utf8 encoding
     * returns null if there was a problem creating it.
     *
     * @param filename including the full path
     * @return new PrintWriter instance
     */
    public static PrintWriter createPrintWriter(String filename) {
        PrintWriter outfile = null;
        try {
            outfile = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(filename, false),
                                    "UTF-8"))); //NON-NLS
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outfile;
    }


    /**
     * @return a URL given the path to a file.
     */
    public static URL getURL(String sPath) {

        return getURL(sPath, true);
    }

    /**
     * @param sPath          the file path to get URL for
     * @param failIfNotFound throws IllegalArgumentException if not found in path
     * @return a URL given the path to an existing file.
     */
    public static URL getURL(String sPath, boolean failIfNotFound) {

        URL url = ClassLoaderSingleton.getClassLoader().getResource(sPath);
        if (url == null && failIfNotFound) {
            throw new IllegalArgumentException("failed to create url for  " + sPath);
        }
        return url;
    }

    /**
     * @param sPath          the file path to get URL for
     * @param failIfNotFound throws IllegalArgumentException if not found in path
     * @return a stream given the path to an existing file.
     */
    public static InputStream getResourceAsStream(String sPath, boolean failIfNotFound) {

        InputStream stream = ClassLoaderSingleton.getClassLoader().getResourceAsStream(sPath);
        if (stream == null && failIfNotFound) {
            throw new IllegalArgumentException("failed to find " + sPath);
        }
        return stream;
    }


    /**
     * @param filename name of file to read from
     * @return text within the file
     * @throws IllegalStateException if could not read the file
     */
    public static String readTextFile(String filename) {
        BufferedReader br = null;
        StringBuilder bldr = new StringBuilder(1000);

        try {
            br = new BufferedReader(new FileReader(filename));

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                bldr.append(sCurrentLine).append('\n');
            }

        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + filename, e);
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return bldr.toString();
    }

    /**
     * Get all files in a directory (not recursive)
     *
     * @param directory full path
     * @return the list of all files in the specified directory
     */
    public static List<File> getFilesInDirectory(String directory) {
        File dir = new File(directory);
        File[] listOfFiles = dir.listFiles();
        List<File> files = new ArrayList<>();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                files.add(file);
            }
        }
        return files;
    }
}
