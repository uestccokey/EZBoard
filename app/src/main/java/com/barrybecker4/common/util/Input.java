/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.util;

import com.barrybecker4.common.format.FormatUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

/**
 * Convenience methods for getting user input entered from the keyboard.
 * Consider using Scanner instead of this class.
 *
 * @author Barry Becker
 */
public final class Input {

    /** private constructor for static class */
    private Input() {}

    /**
     * Get a number from the user.
     *
     * @param prompt query string to prompt the user for a response.
     * @return an integer number.
     */
    public static long getLong(String prompt) throws IOException {
        return getLong(prompt, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * Get a number from the user.
     * Continues to ask until a valid number provided.
     *
     * @param prompt query string to prompt the user for a response.
     * @param min    minimum acceptable value.
     * @param max    the maximum number allowed to be entered.
     * @return an integer number between 0 and max.
     * @throws IOException if error reading
     */
    public static long getLong(String prompt, long min, long max) throws IOException {
        long value = 0;
        boolean valid;

        do {
            System.out.println(prompt);

            InputStreamReader inp = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(inp);
            String str = br.readLine();

            try {
                value = Long.parseLong(str);
                valid = true;
                if (value < min) {
                    System.out.println("You must enter a number greater than "
                            + FormatUtil.formatNumber(min));
                    valid = false;
                } else if (value > max) {
                    System.out.println("That number is too big! It must be smaller than "
                            + FormatUtil.formatNumber(max));
                    valid = false;
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Hey! What kind of number is that? ");
                valid = false;
            }

        } while (!valid);   // give them another chance if not valid.
        return value;
    }

    /**
     * Get a potentially huge number from the user.
     * Continues to ask until a valid number provided.
     * There is mo limit to the amount of precision.
     *
     * @param prompt query string to prompt the user for a response.
     * @return an big integer number
     * @throws IOException if error reading
     */
    public static BigInteger getBigInteger(String prompt) throws IOException {
        BigInteger value = new BigInteger("0");
        boolean valid;

        do {
            System.out.println(prompt);

            InputStreamReader inp = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(inp);
            try {
                value = new BigInteger(br.readLine());
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("That was not a valid number. Try again.");
                valid = false;
            }

        } while (!valid);
        return value;
    }


    /**
     * Get a string from the user.
     * todo: add an optional regexp argument.
     *
     * @return input string.
     * @throws IOException if error reading
     */
    public static String getString(String prompt) throws IOException {

        System.out.println(prompt);

        InputStreamReader inp = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(inp);
        return br.readLine();
    }
}
