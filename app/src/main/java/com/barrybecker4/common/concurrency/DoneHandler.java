package com.barrybecker4.common.concurrency;

/**
 * Called when a task completes with a result
 *
 * @author Barry Becker
 */
public interface DoneHandler<T> {

    void done(T result);
}
