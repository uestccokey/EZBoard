/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.concurrency;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

/**
 * Using this class you should be able to easily parallelize a set of long running allable tasks.
 * Immutable.
 * T - the result object
 *
 * @author Barry Becker
 */
public class CallableParallelizer<T> extends AbstractParallelizer<T> {

    public CallableParallelizer() {
        super();
    }

    /**
     * Construct with specified number of threads.
     *
     * @param numThreads number of thread. Must be 1 or greater. One means not parallelism.
     */
    public CallableParallelizer(int numThreads) {
        super(numThreads);
    }

    /**
     * Invoke all the workers at once and optionally call doneHandler on the results as they complete.
     * Once all the separate threads have completed their assigned work, you may want to commit the results.
     *
     * @param callables   list of workers to execute in parallel.
     * @param doneHandler gets called for each task as it finishes.
     */
    public void invokeAllWithCallback(List<Callable<T>> callables, DoneHandler<T> doneHandler) {
        List<Future<T>> futures = invokeAll(callables);
        ExecutorCompletionService<T> completionService = new ExecutorCompletionService<>(executor);

        for (final Callable<T> callable : callables) {
            completionService.submit(callable);
        }

        try {
            for (int i = 0; i < futures.size(); i++) {
                final Future<T> future = completionService.take();
                try {
                    T result = future.get();
                    if (doneHandler != null && result != null) {
                        doneHandler.done(result);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
