/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.interplolation;

/**
 * @author Barry Becker
 */
public enum InterpolationMethod {
    STEP {
        @Override
        public Interpolator createInterpolator(double[] function) {
            return new StepInterpolator(function);
        }
    },
    LINEAR {
        @Override
        public Interpolator createInterpolator(double[] function) {
            return new LinearInterpolator(function);
        }
    },
    CUBIC {
        @Override
        public Interpolator createInterpolator(double[] function) {
            return new CubicInterpolator(function);
        }
    },
    COSINE {
        @Override
        public Interpolator createInterpolator(double[] function) {
            return new CosineInterpolator(function);
        }
    },
    HERMITE {
        @Override
        public Interpolator createInterpolator(double[] function) {
            return new HermiteInterpolator(function);
        }
    };


    /**
     * Factory method for creating the search strategy to use.
     * Do not call the constructor directly.
     *
     * @return the search method to use
     */
    public abstract Interpolator createInterpolator(double[] function);

}
