/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.format;

import java.text.DecimalFormat;

/**
 * Miscellaneous commonly used static utility methods.
 */
public final class FormatUtil {

    private static final DecimalFormat expFormat_ = new DecimalFormat("0.###E0"); //NON-NLS
    private static final DecimalFormat format_ = new DecimalFormat("###,###.##");
    private static final DecimalFormat intFormat_ = new DecimalFormat("#,###");

    private FormatUtil() {}

    /**
     * Show a reasonable number of significant digits.
     *
     * @param num the number to format.
     * @return a nicely formatted string representation of the number.
     */
    public static String formatNumber(double num) {
        double absnum = Math.abs(num);

        if (absnum == 0) {
            return "0";
        }
        if (absnum > 10000000.0 || absnum < 0.000000001) {
            return expFormat_.format(num);
        }

        if (absnum > 1000.0) {
            format_.setMinimumFractionDigits(0);
            format_.setMaximumFractionDigits(0);
        } else if (absnum > 100.0) {
            format_.setMinimumFractionDigits(1);
            format_.setMaximumFractionDigits(1);
        } else if (absnum > 1.0) {
            format_.setMinimumFractionDigits(1);
            format_.setMaximumFractionDigits(3);
        } else if (absnum > 0.0001) {
            format_.setMinimumFractionDigits(2);
            format_.setMaximumFractionDigits(5);
        } else if (absnum > 0.000001) {
            format_.setMinimumFractionDigits(3);
            format_.setMaximumFractionDigits(8);
        } else {
            format_.setMinimumFractionDigits(6);
            format_.setMaximumFractionDigits(11);
        }

        return format_.format(num);
    }

    /**
     * @param num the number to format.
     * @return a nicely formatted string representation of the number.
     */
    public static String formatNumber(long num) {
        return intFormat_.format(num);
    }

    /**
     * @param num the number to format.
     * @return a nicely formatted string representation of the number.
     */
    public static String formatNumber(int num) {
        return intFormat_.format(num);
    }

    public static void main(String[] args) {
        System.out.println("formatted small number: " + FormatUtil.formatNumber(0.00000003456)); //NON-NLS
        System.out.println("formatted medium number: " + FormatUtil.formatNumber(239909.034983456)); //NON-NLS
        System.out.println("formatted large number: " + FormatUtil.formatNumber(1234981289879875329290.3456)); //NON-NLS
    }
}

