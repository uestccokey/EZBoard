/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.profile;

import com.barrybecker4.common.app.ILog;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Use this class to get performance numbers for your application
 * in order to eliminate bottlenecks. It is typically subclassed in order to
 * identify (using entries) the specific parts of a program that need to be profiled.
 *
 * @author Barry Becker
 */
public class Profiler {

    private final Map<String, ProfilerEntry> hmEntries_ = new HashMap<>();
    private final List<ProfilerEntry> topLevelEntries_ = new LinkedList<>();
    private boolean enabled_ = true;
    private ILog logger_ = null;

    /**
     * Default constructor.
     */
    public Profiler() {}

    /**
     * add a top level entry.
     *
     * @param name of the top level entry
     */
    public void add(String name) {
        ProfilerEntry entry = new ProfilerEntry(name);
        topLevelEntries_.add(entry);
        hmEntries_.put(name, entry);
    }

    /**
     * add an entry into the profiler entry hierarchy.
     *
     * @param name   name for new entry
     * @param parent entry above us.
     */
    public void add(String name, String parent) {
        ProfilerEntry par = getEntry(parent);
        assert par != null : "invalid parent: " + parent;
        ProfilerEntry e = new ProfilerEntry(name);
        par.addChild(e);
        hmEntries_.put(name, e);
    }

    /**
     * @param name the entry for whom we are to start the timing.
     */
    public void start(String name) {
        if (!enabled_) return;
        ProfilerEntry p = getEntry(name);
        p.start();
    }

    /**
     * @param name the entry for which we are to stop the timing and increment the total time.
     */
    public void stop(String name) {
        if (!enabled_) return;
        ProfilerEntry p = getEntry(name);
        p.stop();
    }

    protected ProfilerEntry getEntry(String name) {
        return hmEntries_.get(name);
    }

    /**
     * reset all the timing numbers to 0
     */
    public void resetAll() {
        for (ProfilerEntry entry : topLevelEntries_) {
            entry.resetAll();
        }
    }

    /**
     * pretty print all the performance statistics.
     */
    public void print() {
        if (!enabled_) return;
        for (ProfilerEntry entry : topLevelEntries_) {
            entry.print("", logger_);
        }
    }

    /** turn on/off profiling */
    public void setEnabled(boolean enable) {
        enabled_ = enable;
    }

    public boolean isEnabled() {
        return enabled_;
    }


    public void setLogger(ILog logger) {
        logger_ = logger;
    }

    public void printMessage(String message) {
        if (logger_ != null) {
            logger_.print(message);
        } else {
            System.out.println(message);
        }
    }
}
