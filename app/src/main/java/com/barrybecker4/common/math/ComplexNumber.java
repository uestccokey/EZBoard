/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math;

/**
 * A Complex number is comprised of real and imaginary parts of the form a + bi.
 * Immutable
 *
 * @author Barry Becker
 */
public class ComplexNumber {

    /** The real part */
    private double a;

    /** The imaginary part */
    private double b;

    /**
     * Construct a complex number.
     */
    public ComplexNumber(double realPart, double imaginaryPart) {
        a = realPart;
        b = imaginaryPart;
    }

    /**
     * Copy constructor.
     */
    public ComplexNumber(ComplexNumber c) {
        a = c.a;
        b = c.b;
    }

    /**
     * @return the Real part
     */
    public double getReal() {
        return a;
    }

    /**
     * @return the imaginary part
     */
    public double getImaginary() {
        return b;
    }

    /**
     * @return the magnitude of a complex number. ie.e distance from origin.
     */
    public double getMagnitude() {
        return Math.sqrt(a * a + b * b);
    }

    /**
     * Add another Complex to this one
     *
     * @return the sum of this number plus another..
     */
    public ComplexNumber add(ComplexNumber other) {
        return add(this, other);
    }

    /**
     * @return result of subtracting another ComplexNumber from this one.
     */
    public ComplexNumber subtract(ComplexNumber other) {
        return subtract(this, other);
    }


    /**
     * @return result of multiplying this Complex times another one.
     */
    public ComplexNumber multiply(ComplexNumber other) {
        return multiply(this, other);
    }

    /**
     * @param exponent integer power to raise to
     * @return raise this complex number to the specified exponent (power).
     */
    public ComplexNumber power(int exponent) {
        ComplexNumber current = new ComplexNumber(this);

        for (int i = 1; i < exponent; i++) {
            current = current.multiply(this);
        }
        return current;
    }

    /**
     * @return the result of dividing c1 by c2.
     */
    public static ComplexNumber divide(ComplexNumber c1, ComplexNumber c2) {
        return new ComplexNumber(
                (c1.a * c2.a + c1.b * c2.b) / (c2.a * c2.a + c2.b * c2.b),
                (c1.b * c2.a - c1.a * c2.b) / (c2.a * c2.a + c2.b * c2.b));
    }

    /**
     * @return the sum of two complex numbers.
     */
    public static ComplexNumber add(ComplexNumber c1, ComplexNumber c2) {
        return new ComplexNumber(c1.a + c2.a, c1.b + c2.b);
    }

    /**
     * @return result of subtracting two ComplexNumber from this one.
     */
    public static ComplexNumber subtract(ComplexNumber c1, ComplexNumber c2) {
        return new ComplexNumber(c1.a - c2.a, c1.b - c2.b);
    }

    /**
     * @return result of Multiplying two complex numbers.
     */
    public static ComplexNumber multiply(ComplexNumber c1, ComplexNumber c2) {
        return new ComplexNumber(c1.a * c2.a - c1.b * c2.b, c1.a * c2.b + c1.b * c2.a);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComplexNumber that = (ComplexNumber) o;

        return Double.compare(that.a, a) == 0 && Double.compare(that.b, b) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = a == +0.0d ? 0L : Double.doubleToLongBits(a);
        result = (int) (temp ^ (temp >>> 32));
        temp = b == +0.0d ? 0L : Double.doubleToLongBits(b);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * @return string form.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder().append(a);
        if (Math.abs(a) > 0 && b > 0) {
            sb.append('+');
        }
        sb.append(b).append('i');
        return sb.toString();
    }
}
