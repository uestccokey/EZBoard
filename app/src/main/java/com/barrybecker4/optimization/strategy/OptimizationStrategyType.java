// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.strategy;

import com.barrybecker4.optimization.Optimizer;
import com.barrybecker4.optimization.optimizee.Optimizee;

/**
 * Enum for the different possible Optimization Strategies.
 * There is an optimization strategy class corresponding to each of these types.
 * Detailed explanations for many of these algorithms can be found in
 * How To Solve It: Modern Heuristics  by Michaelwics and Fogel
 *
 * @author Barry Becker
 * @see OptimizationStrategy
 * @see Optimizer
 */
public enum OptimizationStrategyType {

    GLOBAL_SAMPLING("Sparsely sample the space and return the best sample.") {
        @Override
        public OptimizationStrategy getStrategy(Optimizee optimizee, double fitnessRange) {
            GlobalSampleStrategy gsStrategy = new GlobalSampleStrategy(optimizee);
            gsStrategy.setSamplingRate(1000);
            return gsStrategy;
        }
    },
    GLOBAL_HILL_CLIMBING("Start with the best global sampling and hill climb from there.") {
        @Override
        public OptimizationStrategy getStrategy(Optimizee optimizee, double fitnessRange) {
            return new GlobalHillClimbingStrategy(optimizee);
        }
    },
    HILL_CLIMBING("Search method which always marches toward the direction of greatest improvement.") {
        @Override
        public OptimizationStrategy getStrategy(Optimizee optimizee, double fitnessRange) {
            return new HillClimbingStrategy(optimizee);
        }
    },
    SIMULATED_ANNEALING("Marches in the general direction of improvement, but can escape local optima.") {
        @Override
        public OptimizationStrategy getStrategy(Optimizee optimizee, double fitnessRange) {
            SimulatedAnnealingStrategy strategy = new SimulatedAnnealingStrategy(optimizee);
            strategy.setMaxTemperature(fitnessRange / 10.0);
            return strategy;
        }
    },
    TABU_SEARCH("Uses memory of past solutions to avoid searching them again as it marches toward an optimal solution.") {
        @Override
        public OptimizationStrategy getStrategy(Optimizee optimizee, double fitnessRange) {
            throw new AbstractMethodError("Tabu search not yet implemented");
        }
    },
    GENETIC_SEARCH("Uses a genetic algorithm to search for the best solution.") {
        @Override
        public OptimizationStrategy getStrategy(Optimizee optimizee, double fitnessRange) {
            GeneticSearchStrategy strategy = new GeneticSearchStrategy(optimizee);
            strategy.setImprovementEpsilon(fitnessRange / 100000000.0);
            return strategy;
        }
    },
    CONCURRENT_GENETIC_SEARCH("Uses a concurrent genetic algorithm to search for the best solution.") {
        @Override
        public OptimizationStrategy getStrategy(Optimizee optimizee, double fitnessRange) {
            ConcurrentGeneticSearchStrategy strategy = new ConcurrentGeneticSearchStrategy(optimizee);
            strategy.setImprovementEpsilon(fitnessRange / 100000000.0);
            return strategy;
        }
    },
    STATE_SPACE("Searches the state space to find an optima.") {
        @Override
        public OptimizationStrategy getStrategy(Optimizee optimizee, double fitnessRange) {
            throw new AbstractMethodError("State space search not yet implemented");
        }
    },
    BRUTE_FORCE("Tries all possible combinations in order to find the best possible. " +
            "Not possible if parameter space has real values.") {
        @Override
        public OptimizationStrategy getStrategy(Optimizee optimizee, double fitnessRange) {
            return new BruteForceStrategy(optimizee);
        }
    };

    private String description_;

    /**
     * constructor for optimization type enum
     *
     * @param description string description of the optimization strategy.
     */
    OptimizationStrategyType(String description) {
        description_ = description;
    }

    public String getDescription() {
        return description_;
    }

    /**
     * Create an instance of the strategy to use.
     *
     * @param optimizee    the thing to optimize.
     * @param fitnessRange the approximate range (max-min) of the fitness values
     * @return an instance of the strategy to use.
     */
    public abstract OptimizationStrategy getStrategy(Optimizee optimizee, double fitnessRange);
}

