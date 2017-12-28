/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.format;

/**
 * @author Barry Becker
 */
public class DefaultNumberFormatter implements INumberFormatter {

    @Override
    public String format(double number) {
        return FormatUtil.formatNumber(number);
    }
}
