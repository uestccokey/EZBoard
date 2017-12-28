/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math;

/**
 * Represents an n dimensional vector class.
 *
 * @author Barry Becker
 */
public class Vector {

    /** the vector values */
    private double[] data;

    /**
     * Constructor
     *
     * @param length number of elements
     */
    public Vector(int length) {
        if (length <= 0)
            throw new IllegalArgumentException("length must be at least 1");
        data = new double[length];
    }

    /**
     * Constructor
     *
     * @param data array of values to use for data
     */
    public Vector(double[] data) {
        this.data = data;
    }

    public void set(int i, double value) {
        this.data[i] = value;
    }

    public double get(int i) {
        return data[i];
    }

    public void copyFrom(Vector b) {
        System.arraycopy(this.data, 0, b.data, 0, size());
    }

    /**
     * Find the dot product of ourselves with another vector.
     *
     * @return the dot product with another vector
     */
    public double dot(Vector b) {
        checkDims(b);

        double dotProduct = 0.0;
        for (int i = 0; i < size(); i++) {
            dotProduct += data[i] * b.get(i);
        }
        return dotProduct;
    }

    /**
     * Find the normalized dot product with range [-1, 1].
     *
     * @param b vector to dot product with
     * @return the normalized dot product.
     */
    public double normalizedDot(Vector b) {
        double magB = b.magnitude();
        double magThis = this.magnitude();
        System.out.println("for v1=" + this + "v2=" + b + " magThis=" + magThis + " magB=" + magB); //NON-NLS
        double divisor = magThis * magB;
        divisor = (divisor == 0) ? 1.0 : divisor;
        double dot = this.dot(b);
        double normalizedDotProduct = dot / divisor;

        assert normalizedDotProduct >= (-1.0 - MathUtil.EPS_MEDIUM) && normalizedDotProduct <= (1 + MathUtil.EPS_MEDIUM) :
                "Normalized Dot product, " + normalizedDotProduct
                        + ", was outside expected range.\nDot=" + dot + " div=" + divisor + "\nfor v1=" + this + "v2=" + b
                        + "\nmagThis=" + magThis + " magB=" + magB;

        return normalizedDotProduct;
    }

    /**
     * @param factor amount to scale by.
     * @return this Vector, scaled by a constant factor
     */
    public Vector scale(double factor) {
        double[] newData = new double[size()];
        for (int i = 0; i < size(); i++) {
            newData[i] = factor * data[i];
        }
        return new Vector(newData);
    }

    /**
     * @return pairwise sum of this Vector a and b
     */
    public Vector plus(Vector b) {

        checkDims(b);
        double[] d = new double[size()];
        for (int i = 0; i < size(); i++)
            d[i] = this.data[i] + b.data[i];
        return new Vector(d);
    }

    /**
     * @param b vector to find distance to.
     * @return Euclidean distance between this Vector and b
     */
    public double distanceTo(Vector b) {

        checkDims(b);
        double sum = 0.0;
        for (int i = 0; i < size(); i++)
            sum += (this.data[i] - b.data[i]) * (this.data[i] - b.data[i]);
        return Math.sqrt(sum);
    }

    /** @return magnitude of the vector. */
    public double magnitude() {
        double sumOfSquares = 0.0;
        for (int i = 0; i < size(); i++)
            sumOfSquares += this.data[i] * this.data[i];
        return Math.sqrt(sumOfSquares);
    }

    /**
     * @return a vector in the same direction as vec, but with unit magnitude.
     */
    public Vector normalize() {
        double len = this.magnitude();
        Vector unitVec = new Vector(data.length);
        for (int i = 0; i < data.length; i++) {
            unitVec.set(i, data[i] / len);
        }
        return unitVec;
    }

    public int size() {
        return data.length;
    }

    private void checkDims(Vector b) {
        if (this.size() != b.size()) throw new IllegalArgumentException("Dimensions don't match");
    }

    /**
     * @return a string representation of the vector
     */
    public String toString() {
        String s = "";
        for (int i = 0; i < size(); i++) {
            s = s + data[i] + " ";
        }
        return s;
    }
}
