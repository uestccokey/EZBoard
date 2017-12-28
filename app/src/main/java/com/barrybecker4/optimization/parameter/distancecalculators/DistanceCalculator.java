package com.barrybecker4.optimization.parameter.distancecalculators;

import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * @author Barry Becker
 */
public interface DistanceCalculator {
    double calculateDistance(ParameterArray pa1, ParameterArray pa2);
}
