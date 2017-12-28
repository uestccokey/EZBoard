// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.common.concurrency;

/**
 * Class to maintain reference to current worker thread
 * under separate synchronization control.
 */
class ThreadVar {

    private Thread thread;

    /** Constructor */
    ThreadVar(Thread thread) {
        assert thread != null;
        this.thread = thread;
    }

    synchronized Thread get() {
        return thread;
    }

    synchronized void clear() {
        thread = null;
    }

    synchronized void interrupt() {
        if (thread != null) {
            thread.interrupt();
        }
        thread = null;
    }

    synchronized void start() {
        if (thread != null) {
            thread.start();
        }
    }
}