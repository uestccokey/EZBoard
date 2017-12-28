// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter;

import com.barrybecker4.optimization.parameter.types.Parameter;

/**
 * Implemented by classes that do something when a parameter gets changed.
 *
 * @author Barry Becker
 */
public interface ParameterChangeListener {

    void parameterChanged(Parameter param);
}
