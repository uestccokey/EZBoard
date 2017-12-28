/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.concurrency;

/**
 * @author Barry Becker
 */
public class ThreadUtil {

    private ThreadUtil() {}

    /**
     * Cause this thread to sleep for specified amount of time while other threads run.
     *
     * @param millis number of seconds to sleep
     */
    public static void sleep(int millis) {
        if (millis > 0) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
