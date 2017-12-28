/** Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Using this class you should be able to easily parallelize a set of long running tasks.
 * Immutable.
 * T - the result
 *
 * @author Barry Becker
 */
public class RunnableParallelizer extends AbstractParallelizer<Object> {

    public RunnableParallelizer() {
        super();
    }

    /**
     * Constructor
     *
     * @param numThreads the number of threads that are assumed available on the hardware.
     */
    public RunnableParallelizer(int numThreads) {
        super(numThreads);
    }

    /**
     * Invoke all the workers at once and optionally call doneHandler on the results as they complete.
     * Once all the separate threads have completed their assigned work, you may want to commit the results.
     *
     * @param workers list of workers to execute in parallel.
     */
    public void invokeAllRunnables(List<Runnable> workers) {
        // convert the runnables to callables so the invokeAll api works
        List<Callable<Object>> callables = new ArrayList<>(workers.size());
        for (Runnable r : workers) {
            callables.add(Executors.callable(r, null));
        }

        List<Future<Object>> futures = invokeAll(callables);

        ExecutorCompletionService<Object> completionService = new ExecutorCompletionService<>(executor);

        for (final Callable<Object> callable : callables) {
            completionService.submit(callable);
        }

        try {
            for (int i = 0; i < futures.size(); i++) {
                final Future<Object> future = completionService.take();
                try {
                    future.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
