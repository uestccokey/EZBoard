// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.strategy;

import com.barrybecker4.common.concurrency.RunnableParallelizer;
import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Concurrent (i.e. parallelized) Genetic Algorithm (evolutionary) optimization strategy.
 * Many different strategies are possible to alter the population for each successive iteration.
 * The 2 primary ones that I use here are unary mutation and cross-over.
 * See Chapter 6 in "How to Solve it: Modern Heuristics" for more info.
 *
 * @author Barry Becker
 */
public class ConcurrentGeneticSearchStrategy extends GeneticSearchStrategy {

    /**
     * Constructor
     *
     * @param optimizee the thing to be optimized.
     */
    public ConcurrentGeneticSearchStrategy(Optimizee optimizee) {
        super(optimizee);
    }


    /**
     * Note: this method assigns a fitness value to each member of the population.
     * <p>
     * Evaluate the members of the population - either directly, or by
     * comparing them against the initial params value passed in (including params).
     * <p>
     * Create a thread for each evaluation and don't continue until they are all done (countDown latch or gate);
     *
     * @param population   the population to evaluate
     * @param previousBest the best solution from the previous iteration
     * @return the new best solution.
     */
    @Override
    protected ParameterArray evaluatePopulation(List<ParameterArray> population, ParameterArray previousBest) {
        ParameterArray bestFitness = previousBest;

        RunnableParallelizer parallelizer = new RunnableParallelizer();

        List<Runnable> workers = new ArrayList<>(population.size());

        for (ParameterArray candidate : population) {
            workers.add(new EvaluationWorker(candidate, previousBest));
        }

        // blocks until all Callables are done running.
        parallelizer.invokeAllRunnables(workers);

        for (Runnable worker : workers) {

            EvaluationWorker eworker = (EvaluationWorker) worker;
            double fitness = eworker.getResult();
            if (fitness < bestFitness.getFitness()) {
                bestFitness = eworker.getCandidate();
            }
        }

        return bestFitness.copy();
    }


    /** Does the evaluation for each candidate in a different thread. */
    class EvaluationWorker implements Runnable {

        private double fitness;
        ParameterArray candidate;
        ParameterArray params;

        public EvaluationWorker(ParameterArray candidate, ParameterArray params) {
            this.params = params;
            this.candidate = candidate;
        }

        public void run() {

            if (optimizee_.evaluateByComparison()) {
                fitness = optimizee_.compareFitness(candidate, params);
            } else {
                fitness = optimizee_.evaluateFitness(candidate);
            }
            candidate.setFitness(fitness);
        }

        public double getResult() {
            return fitness;
        }

        public ParameterArray getCandidate() {
            return candidate;
        }
    }
}


