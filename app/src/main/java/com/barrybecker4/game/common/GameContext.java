/** Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common;

import com.barrybecker4.common.app.ILog;

import java.io.FileNotFoundException;
import java.util.Random;

/**
 * Manage game context info such as logging, debugging, resources, and profiling.
 * Perhaps use java properties or config file to define options.
 *
 * @author Barry Becker
 */
public final class GameContext {

    /** logger object. Use console by default. */
    private static ILog sLogger = new ILog() {

        @Override
        public void setDestination(int logDestination) {
        }

        @Override
        public int getDestination() {
            return 0;
        }

        @Override
        public void setLogFile(String fileName) throws FileNotFoundException {
        }

        @Override
        public void setStringBuilder(StringBuilder bldr) {
        }

        @Override
        public void print(int logLevel, int appLogLevel, String message) {
        }

        @Override
        public void println(int logLevel, int appLogLevel, String message) {
        }

        @Override
        public void print(String message) {
        }

        @Override
        public void println(String message) {
        }
    };

    /** Make sure that the program runs in a reproducible way by always starting from the same random seed. */
    private static Random RANDOM = new Random(0);

    /** if greater than 0, then debug mode is on. the higher the number, the more info that is printed. */
    private static final int DEBUG = 0;

    /** now the variable forms of the above defaults */
    private static int debug_ = DEBUG;

    /** if true, then profiling performance statistics will be printed to the console while running. */
    private static final boolean PROFILING = false;
    private static boolean profiling_ = PROFILING;

    /** private constructor for singleton. */
    private GameContext() {}

    /**
     * @return the level of debugging in effect
     */
    public static int getDebugMode() {
        return debug_;
    }

    /**
     * @param debug the debug level. 0 means all logging.
     */
    public static void setDebugMode(int debug) {
        debug_ = debug;
    }

    /**
     * @return true if profiling stats are being shown after every move
     */
    public static boolean isProfiling() {
        return profiling_;
    }

    /**
     * @param prof whether or not to turn on profiling
     */
    public static void setProfiling(boolean prof) {
        profiling_ = prof;
    }

    /**
     * @param logger the logging device. Determines where the output goes.
     */
    public static void setLogger(ILog logger) {
        assert logger != null;
        sLogger = logger;
    }

    /**
     * @return the logging device to use.
     */
    public static ILog getLogger() {
        return sLogger;
    }

    /**
     * log a message using the internal logger object
     */
    public static void log(int logLevel, String message) {
        sLogger.print(logLevel, getDebugMode(), message);
    }

    public static Random random() {
        return RANDOM;
    }

    public static void setRandomSeed(int seed) {
        RANDOM = new Random(seed);
    }
}
