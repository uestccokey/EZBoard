// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization;

import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.optimization.strategy.OptimizationStrategy;
import com.barrybecker4.optimization.strategy.OptimizationStrategyType;

/**
 * This class (the optimizer) uses a specified optimization strategy to optimize something (the optimizee).
 *
 * @author Barry Becker
 * @see OptimizationStrategyType for a list of the possible algorithms.
 * <p>
 * This class uses the delegation design pattern rather than inheritance
 * so that it can be reused across many classes. For example, an optimize
 * method could have been added to the game/TwoPlayerController class, and all the subclasses
 * of TwoPlayerController would be able to use it. However, by having the optimization
 * classes in their own package, they can be used by a variety of projects to do
 * optimization. Also it abstracts the concept of optimization and as a result
 * makes it easy to work on independently. For example, this library is used to
 * optimize the motion of the snake in com.barrybecker4.snake (in bb4-simulations),
 * the firing of a trebuchet (in bb4-simulations), and solve
 * puzzles efficiently in bb4-puzzles.
 * <p>
 * This class also acts as a facade to the optimization package. The use of this package
 * really does not need to direclty construct or use the different optimization strategy classes.
 * <p>
 * Details of the optimization algorithms can be found in
 * How To Solve It: Modern Heuristics  by Michaelwics and Fogel
 */
public class Optimizer {

    /** The thing to be optimized */
    Optimizee optimizee_;

    protected Logger logger_;

    protected OptimizationListener listener_;


    /**
     * Constructor
     * No log file specified in this constructor. (use this version if running in unsigned applet).
     *
     * @param optimizee the thing to be optimized.
     */
    public Optimizer(Optimizee optimizee) {
        optimizee_ = optimizee;
    }

    /**
     * Constructor
     *
     * @param optimizee           the thing to be optimized.
     * @param optimizationLogFile the file that will record the results
     */
    public Optimizer(Optimizee optimizee, String optimizationLogFile) {
        optimizee_ = optimizee;
        logger_ = new Logger(optimizationLogFile);
    }

    public Optimizee getOptimizee() {
        return optimizee_;
    }

    /**
     * This method will construct an optimization strategy object of the specified type and run it.
     *
     * @param optimizationType the type of search to perform
     * @param params           the initialGuess at the solution. Also defines the bounds of the search space.
     * @param fitnessRange     the approximate range (max-min) of the fitness values
     * @return the solution to the optimization problem.
     */
    public ParameterArray doOptimization(OptimizationStrategyType optimizationType,
                                         ParameterArray params, double fitnessRange) {

        OptimizationStrategy optStrategy = optimizationType.getStrategy(optimizee_, fitnessRange);
        if (logger_ != null) {
            logger_.initialize(params);
            optStrategy.setLogger(logger_);
        }

        optStrategy.setListener(listener_);
        return optStrategy.doOptimization(params, fitnessRange);
    }

    public void setListener(OptimizationListener l) {
        listener_ = l;
    }
}
