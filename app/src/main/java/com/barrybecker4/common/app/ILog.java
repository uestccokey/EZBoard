/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.app;

import java.io.FileNotFoundException;

/**
 * Provide support for general logging.
 *
 * @author Barry Becker
 */
public interface ILog {

    /**
     * Set the log destination
     * Should eventually allow multiples using | to combine the hex constants.
     */
    void setDestination(int logDestination);

    /**
     * @return the current logging destination
     */
    int getDestination();

    /**
     * @param fileName the name of the file to send the output to.
     */
    void setLogFile(String fileName) throws FileNotFoundException;

    /** for logging to a string. */
    void setStringBuilder(StringBuilder bldr);

    /**
     * Log a message to the logDestination
     *
     * @param logLevel    message will only be logged if this number is less than the application logLevel (debug_)
     * @param appLogLevel the applications log level.
     * @param message     the message to log
     */
    void print(int logLevel, int appLogLevel, String message);

    /**
     * Log a message to the logDestination followed by a newline.
     *
     * @param logLevel    message will only be logged if this number is less than the application logLevel (debug_)
     * @param appLogLevel the applications log level.
     * @param message     the message to log
     */
    void println(int logLevel, int appLogLevel, String message);

    void print(String message);

    void println(String message);
}



