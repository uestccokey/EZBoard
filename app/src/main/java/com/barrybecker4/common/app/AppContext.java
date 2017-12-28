// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.common.app;

import com.barrybecker4.common.i18n.MessageContext;

import java.text.NumberFormat;
import java.util.List;

/**
 * Manage application context such as logging, debugging, resources.
 *
 * @author Barry Becker
 */
public final class AppContext {

    /** logger object. */
    private static ILog logger_;

    /** if greater than 0, then debug mode is on. the higher the number, the more info that is printed. */
    private static final int DEBUG = 0;

    /** now the variable forms of the above defaults */
    private static int debug_ = DEBUG;

    private static MessageContext messageContext_;

    /**
     * Initialize the app context once a the start of a program
     *
     * @param localeName    name of the locale to use (ENGLISH, GERMAN, etc)
     * @param resourcePaths locations of the properties file in the classpath pointing to message bundles
     * @param logger        logging implementation
     */
    public static void initialize(String localeName, List<String> resourcePaths, ILog logger) {
        assert resourcePaths != null;
        assert logger != null;
        logger_ = logger;

        messageContext_ = new MessageContext(resourcePaths);
        messageContext_.setLogger(logger_);
        messageContext_.setDebugMode(debug_);
        messageContext_.setLocale(localeName);
    }

    /** Allow setting a custom message context for testing purposes */
    public static void injectMessageContext(MessageContext context) {
        messageContext_ = context;
    }

    public static boolean isInitialized() {
        return logger_ != null;
    }

    /**
     * @return the level of debugging in effect
     */
    public static int getDebugMode() {
        return debug_;
    }

    /**
     * @param debug the debug level to use. If 0, then all logging performed.
     */
    public static void setDebugMode(int debug) {
        debug_ = debug;
    }

    /**
     * log a message using the internal logger object
     */
    public static void log(int logLevel, String message) {
        assert logger_ != null : "Must set a logger before logging";
        logger_.print(logLevel, getDebugMode(), message);
    }

    public static NumberFormat getCurrencyFormat() {
        return NumberFormat.getCurrencyInstance(messageContext_.getLocale());
    }

    /**
     * @param key message key
     * @return the localized message label
     */
    public static String getLabel(String key) {
        return messageContext_.getLabel(key);
    }

    /**
     * Use this version if there are parameters to the localized string
     *
     * @param key message key
     * @return the localized message label
     */
    public static String getLabel(String key, Object[] params) {
        return messageContext_.getLabel(key, params);
    }

    /** private constructor for all static class. */
    private AppContext() {}
}
