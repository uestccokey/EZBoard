/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math;

/**
 * Different sorts of wave forms.
 *
 * @author Barry Becker
 */
public enum WaveType {

    SINE_WAVE("Sine wave") {
        @Override
        public double calculateOffset(double theta) {
            return (Math.sin(theta));
        }
    },
    SQUARE_WAVE("Square Wave") {
        @Override
        public double calculateOffset(double theta) {
            return (Math.sin(theta) > 0.0) ? 1.0 : -1.0;
        }
    },
    SAWTOOTH_WAVE("Sawtooth Wave") {
        @Override
        public double calculateOffset(double theta) {
            double t = theta / Math.PI / 2;
            return 2 * (t - Math.floor(t + 0.5));
        }
    },
    TRIANGLE_WAVE("Triangle Wave") {
        @Override
        public double calculateOffset(double theta) {
            return Math.abs(SAWTOOTH_WAVE.calculateOffset(theta));
        }
    };

    private String name;

    WaveType(String name) {
        this.name = name;
    }

    public abstract double calculateOffset(double theta);

    public String toString() {
        return name;
    }
}
