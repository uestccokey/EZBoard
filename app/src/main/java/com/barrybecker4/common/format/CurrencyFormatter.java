/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.format;

import java.util.Currency;
import java.util.Locale;

/**
 * @author Barry Becker
 */
public class CurrencyFormatter implements INumberFormatter {

    private static final String CURRENCY_SYMBOL = Currency.getInstance(Locale.US).getSymbol();

    @Override
    public String format(double number) {
        String formattedNumber = FormatUtil.formatNumber(number);
        return CURRENCY_SYMBOL + formattedNumber;
    }

    /** for testing */
    public static void main(String[] args) {
        CurrencyFormatter fmtr = new CurrencyFormatter();

        for (double value = 50.0; value < 1000.0; value *= 1.1) {
            System.out.println(value + " formatted = " + fmtr.format(value)); //NON-NLS
        }
    }
}