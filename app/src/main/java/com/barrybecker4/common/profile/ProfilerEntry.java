/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.profile;

import com.barrybecker4.common.app.ILog;
import com.barrybecker4.common.format.FormatUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents the timing numbers for a named region of the code.
 *
 * @author Barry Becker
 */
public class ProfilerEntry {

    private static final String INDENT = "    ";

    /** the name of this profiler entry */
    private final String name_;

    private long startTime_ = 0;

    /** the total time used by this named code section while the app was running */
    private long totalTime_ = 0;

    private final List<ProfilerEntry> children_ = new LinkedList<>();

    /** Constructor */
    public ProfilerEntry(String name) {
        name_ = name;
    }

    protected void addChild(ProfilerEntry child) {
        children_.add(child);
    }

    public void start() {
        startTime_ = System.currentTimeMillis();
    }

    public void stop() {
        totalTime_ += System.currentTimeMillis() - startTime_;
    }

    public long getTime() {
        return totalTime_;
    }

    public double getTimeInSeconds() {
        return (double) totalTime_ / 1000.0;
    }

    protected void resetAll() {
        totalTime_ = 0;
        for (ProfilerEntry p : children_) {
            p.resetAll();
        }
    }

    public void print() {
        print("", null);
    }

    public void print(String indent, ILog logger) {

        String text = indent + getFormattedTime();
        if (logger == null)
            System.out.println(text);
        else
            logger.println(text);

        long totalChildTime = 0;
        for (ProfilerEntry pe : children_) {
            totalChildTime += pe.getTime();
            pe.print(indent + INDENT, logger);
        }

        assert (totalChildTime <= 1.0 * totalTime_) : "The sum of the child times(" + totalChildTime
                + ") cannot be greater than the parent time (" + totalTime_ + ") for entry '" + name_ + "'. " +
                "child entries =" + children_;
    }

    public String toString() {
        return getFormattedTime();
    }

    private String getFormattedTime() {
        double seconds = getTimeInSeconds();
        return "Time for " + name_ + " : " + FormatUtil.formatNumber(seconds) + " seconds"; //NON-NLS
    }
}