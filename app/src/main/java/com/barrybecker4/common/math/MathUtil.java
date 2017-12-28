/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math;

import java.math.BigInteger;
import java.util.Random;

/**
 * Some supplemental mathematics routines.
 * Static util class.
 *
 * @author Barry Becker
 */
public final class MathUtil {

    /** Java double precision cannot accurately represent more than 16 decimal places */
    public static final double EPS = 0.0000000000000001;

    public static final double EPS_MEDIUM = 0.0000000001;

    public static final double EPS_BIG = 0.0001;

    public static final Random RANDOM = new Random(1);

    /** Used in calculating log base 10. */
    private static final double LOG10SCALE = 1.0 / Math.log(10.0);

    /** private constructor for static util class */
    private MathUtil() {}

    /**
     * @param val the value to find log base 10 of.
     * @return log base 10 of the specified value.
     */
    public static double log10(double val) {
        return Math.log(val) * LOG10SCALE;
    }

    /**
     * @param val the value to find 10 to the power of
     * @return 10 to the val power.
     */
    public static double exp10(double val) {
        return Math.pow(10.0, val);
    }

    /**
     * @return the greatest common divisor of 2 longs (may be negative).
     */
    public static long gcd(long a, long b) {

        if (a == 0 && b == 0) return 1;
        if (a < 0) return gcd(-a, b);
        if (b < 0) return gcd(a, -b);
        if (a == 0) return b;
        if (b == 0) return a;
        if (a == b) return a;
        if (b < a) return gcd(b, a);

        return gcd(a, b % a);
    }

    /**
     * @param a numberator
     * @param b denominator
     * @return the least common multiple of a and b
     */
    public static long lcm(long a, long b) {
        return Math.abs(a * b) / gcd(a, b);
    }

    /**
     * Find the least common multiple of specified values.
     *
     * @param values values to find least common multiple of
     * @return the least common multiple of values[0], values[1],... values[i].
     */
    public static long lcm(int[] values) {

        long result = 1;
        for (final int v : values) {
            result = lcm(result, v);
        }
        return result;
    }

    /**
     * Implementation of c(m,n)
     *
     * @param m total number of items
     * @param n number of items to choose where order does not matter
     * @return number of combinations
     */
    public static BigInteger combination(int m, int n) {
        int diff = m - n;
        BigInteger numerator;
        BigInteger denominator;
        if (n > diff) {
            numerator = MathUtil.bigPermutation(m, n);
            denominator = MathUtil.bigFactorial(diff);
        } else {
            numerator = MathUtil.bigPermutation(m, diff);
            denominator = MathUtil.bigFactorial(n);
        }

        return numerator.divide(denominator);
    }

    /**
     * factorial function.
     * 0! = 1 (http://www.zero-factorial.com/whatis.html)
     * This could be a recursive function, but it would be slow and run out of memory for large num.
     *
     * @param num number to take factorial of
     * @return num!
     */
    public static long factorial(int num) {
        assert num >= 0;
        if (num == 0) return 1;
        int ct = num;
        long f = 1;
        while (ct > 1) {
            f *= ct;
            ct--;
        }
        return f;
    }

    /**
     * factorial function.
     * 0! = 1 (http://www.zero-factorial.com/whatis.html)
     * This could be a recursive function, but it would be slow and run out of memory for large num.
     *
     * @param num number to take factorial of
     * @return num!
     */
    public static BigInteger bigFactorial(int num) {
        assert num >= 0;
        if (num == 0) return BigInteger.ONE;
        BigInteger ct = new BigInteger(Integer.toString(num));
        BigInteger f = BigInteger.ONE;
        while (ct.compareTo(BigInteger.ONE) > 0) {
            f = f.multiply(ct);
            ct = ct.subtract(BigInteger.ONE);
        }
        return f;
    }

    /**
     * permutation function computes a!/b!.
     * 0! = 1 (http://www.zero-factorial.com/whatis.html)
     *
     * @param a number of items to select from
     * @param b number of items to select order being important.
     * @return a!/b!
     */
    public static long permutation(int a, int b) {
        assert a > 0;
        assert a > b;
        long f = a;
        int anew = a - 1;
        while (anew > b) {
            f *= anew;
            anew--;
        }
        return f;
    }

    /**
     * permutation function computes a!/b!.
     * 0! = 1 (http://www.zero-factorial.com/whatis.html)
     *
     * @param a number of items to select from
     * @param b number of items to select order being important.
     * @return a!/b!
     */
    public static BigInteger bigPermutation(int a, int b) {
        assert a > 0;
        assert a > b;
        BigInteger f = new BigInteger(Integer.toString(a));
        int anew = a - 1;
        //BigInteger anew = new BigInteger(a-1);
        while (anew > b) {
            f = f.multiply(new BigInteger(Integer.toString(anew)));
            anew--;
        }
        return f;
    }
}