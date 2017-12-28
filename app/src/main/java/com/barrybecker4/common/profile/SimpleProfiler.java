/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.profile;

/**
 * Use this class to get a single performance number for your application.
 * This profiler just contains a single top level entry for a single timing number.
 *
 * @author Barry Becker
 */
public class SimpleProfiler extends Profiler {

    static final String ROOT = "totalTime"; //NON-NLS

    /**
     * Default constructor.
     */
    public SimpleProfiler() {
        super.add(ROOT);
    }

    public void start() {
        start(ROOT);
    }

    public void stop() {
        stop(ROOT);
    }
}
