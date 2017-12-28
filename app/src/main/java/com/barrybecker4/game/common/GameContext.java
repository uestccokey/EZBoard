/** Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common;

import com.barrybecker4.common.app.ILog;

import java.io.FileNotFoundException;
import java.util.Random;

//import com.barrybecker4.sound.Instruments;
//import com.barrybecker4.sound.MusicMaker;
//import com.barrybecker4.ui.util.Log;

/**
 * Manage game context info such as logging, debugging, resources, and profiling.
 * Perhaps use java properties or config file to define options.
 *
 * @author Barry Becker
 */
public final class GameContext {

    /** logger object. Use console by default. */
    private static ILog logger_ = new ILog() {
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

//    /**
//     * Use sound effects if true.
//     * Probably need to turn this off when deploying in applet form to avoid security errors.
//     */
//    private static final boolean useSound_ = false;

    /** this is a singleton. It generates the sounds. */
//    private static MusicMaker musicMaker_ = null;

    /** Make sure that the program runs in a reproducible way by always starting from the same random seed. */
    private static Random RANDOM = new Random(0);


//    static {
//        if (useSound_) {
//            getMusicMaker().stopAllSounds();
//            getMusicMaker().startNote(Instruments.SEASHORE, 40, 2, 3);
//        }
//    }

    //    public static final String GAME_ROOT = "game/source/com/barrybecker4/game/"; // NON-NLS
    public static final String GAME_RESOURCE_ROOT = "com/barrybecker4/game/";   // NON-NLS

    /** if greater than 0, then debug mode is on. the higher the number, the more info that is printed. */
    private static final int DEBUG = 0;

    /** now the variable forms of the above defaults */
    private static int debug_ = DEBUG;

    /** if true, then profiling performance statistics will be printed to the console while running. */
    private static final boolean PROFILING = false;
    private static boolean profiling_ = PROFILING;

    private static final String COMMON_MESSAGE_BUNDLE = "com.barrybecker4.game.common.resources.coreMessages"; // NON-NLS
//    private static MessageContext messageContext_ = new MessageContext(COMMON_MESSAGE_BUNDLE);

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
        logger_ = logger;
    }

    /**
     * @return the logging device to use.
     */
    public static ILog getLogger() {
        return logger_;
    }

    /**
     * log a message using the internal logger object
     */
    public static void log(int logLevel, String message) {
        logger_.print(logLevel, getDebugMode(), message);
    }

//    /**
//     * @return true if sound is not turned off.
//     */
//    public static boolean getUseSound() {
//        return useSound_;
//    }

//    /**
//     * @return use this to add cute sound effects.
//     */
//    public static synchronized MusicMaker getMusicMaker() {
//        if (musicMaker_ == null) {
//            musicMaker_ = new MusicMaker();
//        }
//        return musicMaker_;
//    }
//
//    /**
//     * This method causes the appropriate message bundles to
//     * be loaded for the game specified.
//     * @param gameName the current game
//     */
//    public static void loadResources(String gameName) {
//        log(0, "loadGameResources gameName=" + gameName);             // NON-NLS
//        GamePlugin plugin = PluginManager.getInstance().getPlugin(gameName);
//        assert plugin != null : "Could not find plugin for " + gameName;
//        log(0, "plugin = " + plugin);     // NON-NLS
//        log(0, "gameName=" + gameName + " plugin=" + plugin);  // NON-NLS
//        String resourcePath = plugin.getMsgBundleBase();
//        log(2, "searching for " + resourcePath);          // NON-NLS
//
//        messageContext_.setLogger(logger_);
//        messageContext_.setDebugMode(debug_);
//        messageContext_.addResourcePath(resourcePath);
//        messageContext_.addResourcePath("com.barrybecker4.ui.message");   // NON-NLS
//        log(0, "done loading resources");
//    }
//
//    /**
//     * set the current locale .
//     * @param locale the locale to use
//     */
//    public static void setLocale(LocaleType locale) {
//        messageContext_.setLocale(locale);
//    }
//
//    public static NumberFormat getCurrencyFormat() {
//        return NumberFormat.getCurrencyInstance(messageContext_.getLocale());
//    }

    /**
     * @param key message key
     * @return the localized message label
     */
    public static String getLabel(String key) {
        return key;
    }

//    /**
//     * Looks up an {@link LocaleType} for a given locale name.
//     * @param name name of the locale to get localeType for
//     * @param finf fail if not found.
//     * @return locale
//     * @throws Error if the name is not a member of the enumeration
//     */
//    public static LocaleType getLocale(String name, boolean finf) {
//
//        return messageContext_.getLocale(name, finf);
//    }

    public static Random random() {
        return RANDOM;
    }

    public static void setRandomSeed(int seed) {
        RANDOM = new Random(seed);
    }
}
