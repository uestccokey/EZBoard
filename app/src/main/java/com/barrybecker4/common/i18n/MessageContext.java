/** Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.i18n;

import com.barrybecker4.common.app.ILog;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

//import javax.swing.JComponent;

/**
 * Manage access to localized message bundles.
 * When creating an instance specify the paths to the resource bundles to use.
 *
 * @author Barry Becker
 */
public class MessageContext {

    public static final LocaleType DEFAULT_LOCALE = LocaleType.ENGLISH;

    /** logger object. Use console by default. */
    private ILog logger_;

    /** debug level */
    private int debug_ = 0;

    /** the list of paths the define where to get the messageBundles */
    private List<String> resourcePaths_;

    /** the list of bundles to look for messages in */
    private List<ResourceBundle> messagesBundles_;

    private LocaleType currentLocale_ = DEFAULT_LOCALE;


    /**
     * Constructor
     *
     * @param resourcePath path to message bundle
     */
    public MessageContext(String resourcePath) {
        this(new ArrayList<>(Collections.singletonList(resourcePath)));
    }

    /**
     * Constructor
     *
     * @param resourcePaths list of paths to message bundles
     */
    public MessageContext(List<String> resourcePaths) {
        resourcePaths_ = resourcePaths;
        messagesBundles_ = new ArrayList<>();
    }

    /**
     * @param resourcePath another resource path to get a message bundle from.
     */
    public void addResourcePath(String resourcePath) {
        if (!resourcePaths_.contains(resourcePath)) {
            resourcePaths_.add(resourcePath);
            messagesBundles_.clear();
        }
    }

    public void setDebugMode(int debugMode) {
        debug_ = debugMode;
    }

    /**
     * @param logger the logging device. Determines where the output goes.
     */
    public void setLogger(ILog logger) {
        assert logger != null;
        logger_ = logger;
    }


    private void log(int logLevel, String message) {
        if (logger_ == null) {
            throw new RuntimeException("Set a logger on the MessageContext before calling log.");
        }
        logger_.print(logLevel, debug_, message);
    }

    /**
     * Set or change the current locale.
     *
     * @param localeName name locale to use (something like ENGLISH, GERMAN, etc)
     */
    public void setLocale(String localeName) {
        setLocale(getLocale(localeName, true));
    }

    /**
     * Set or change the current locale.
     *
     * @param locale locale to use
     */
    public void setLocale(LocaleType locale) {
        currentLocale_ = locale;
        messagesBundles_.clear();
        initMessageBundles(currentLocale_);
//        JComponent.setDefaultLocale(currentLocale_.getLocale());
    }

    public Locale getLocale() {
        return currentLocale_.getLocale();
    }

    /**
     * Look first in the common message bundle.
     * If not found there, look in the application specific bundle if there is one.
     *
     * @param key the message key to find in resource bundle.
     * @return the localized message label
     */
    public String getLabel(String key) {
        return getLabel(key, null);
    }

    /**
     * Look first in the common message bundle.
     * If not found there, look in the application specific bundle if there is one.
     *
     * @param key    the message key to find in resource bundle.
     * @param params typically a list of string sto use a parameters to the template defined by the message from key.
     * @return the localized message label
     */
    public String getLabel(String key, Object[] params) {
        String label = key;
        if (messagesBundles_.isEmpty()) {
            initMessageBundles(currentLocale_);
        }
        boolean found = false;
        int numBundles = messagesBundles_.size();
        int ct = 0;
        while (!found && ct < numBundles) {
            ResourceBundle bundle = messagesBundles_.get(ct++);
            if (bundle.containsKey(key)) {
                label = bundle.getString(key);
                if (params != null) {
                    MessageFormat formatter = new MessageFormat(label, currentLocale_.getLocale());
                    label = formatter.format(params);
                }
                found = true;
            }
        }

        if (!found) {
            String msg = "Could not find label for " + key + " among " + resourcePaths_.toString();   // NON-NLS
            log(0, msg);
            throw new MissingResourceException(msg, resourcePaths_.toString(), key);
        }
        return label;
    }


    private void initMessageBundles(LocaleType locale) {

        for (String path : resourcePaths_) {
            ResourceBundle bundle = ResourceBundle.getBundle(path, locale.getLocale());
            if (bundle == null) {
                throw new IllegalArgumentException("Messages bundle for " + path + " was not found.");
            }
            messagesBundles_.add(bundle);
        }

//        JComponent.setDefaultLocale(locale.getLocale());
    }

    /**
     * Looks up an {@link LocaleType} for a given locale name.
     *
     * @param finf fail if not found.
     * @return locale the name of a local. Something like ENGLISH, GERMAN, etc
     * @throws Error if the name is not a member of the enumeration
     */
    public LocaleType getLocale(String name, boolean finf) {
        LocaleType type; // english is the default
        try {
            type = LocaleType.valueOf(name);
        } catch (IllegalAccessError e) {
            log(0, "***************");
            log(0, name + " is not a valid locale. We currently only support: ");  // NON-NLS
            LocaleType[] values = LocaleType.values();
            for (final LocaleType newVar : values) {
                log(0, newVar.toString());
            }
            log(0, "Defaulting to English.");  // NON-NLS
            log(0, "***************");
            assert (!finf);
            throw e;
        }
        return type;
    }
}