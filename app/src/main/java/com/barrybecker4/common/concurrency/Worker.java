/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.concurrency;

/**
 * Worker is an abstract class that you subclass to
 * perform (usually gui related) work in a dedicated thread.  For
 * instructions on and examples of using this class, see:
 * <p>
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 * <p>
 * You must invoke start() on the Worker after creating it.
 * ----
 * I have modified the original Worker class so that it no longer
 * depends on Swing (hence the new name). I sometimes want to use this
 * class in a server process. So if you are using it on the gui make sure
 * that the body of the finished method is called from SwingUtilities.invokeLater().
 * -Barry
 */
public abstract class Worker {

    /** value to return after asynchronous computation. See getValue(), setValue() */
    private Object returnValue = null;

    /** worker thread under separate synchronization control. */
    private final ThreadVar threadVar;

    /**
     * Constructor.
     * Start a thread that will call the {@code construct} method and then exit.
     */
    public Worker() {
        Runnable doConstruct = new Runnable() {

            @Override
            public void run() {
                try {
                    returnValue = construct();
                } finally {
                    threadVar.clear();
                }

                // old: SwingUtilities.invokeLater(doFinished);
                // Now call directly, but if the body of finished is in the ui,
                // it should call SwingUtilities.invokeLater()
                finished();
            }
        };

        Thread thread = new Thread(doConstruct);
        thread.setName("Worker Thread"); //NON-NLS
        threadVar = new ThreadVar(thread);
    }

    public boolean isWorking() {
        return getValue() == null;
    }

    /**
     * @return the value produced by the worker thread, or null if it hasn't been constructed yet.
     */
    protected synchronized Object getValue() {
        return returnValue;
    }

    /**
     * Compute the value to be returned by the {@code get} method.
     *
     * @return the result. Must not be null.
     */
    public abstract Object construct();

    /**
     * Start the worker thread.
     */
    public void start() {
        threadVar.start();
    }

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the {@code construct} method has returned.
     */
    public void finished() {
        // intentionally empty
    }

    /**
     * Interrupts the worker thread.  Call this method
     * to force the worker to stop what it's doing.
     */
    public void interrupt() {
        threadVar.interrupt();
    }

    /**
     * Return the value created by the {@code construct} method.
     * Returns null if either the constructing thread or the current
     * thread was interrupted before a value was produced.
     *
     * @return the value created by the {@code construct} method
     */
    public Object get() {
        while (true) {
            Thread thread = threadVar.get();
            if (thread == null) {
                return getValue();
            }
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // propagate
                return null;
            }
        }
    }
}
