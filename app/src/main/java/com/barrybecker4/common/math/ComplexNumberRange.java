/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math;

/**
 * A complex number range represents a box on the complex plane.
 * Immutable.
 *
 * @author Barry Becker
 */
public class ComplexNumberRange {

    private ComplexNumber point1_;
    private ComplexNumber point2_;
    private ComplexNumber extent_;

    /**
     * init with min and max values of the range.
     *
     * @param point1 one value for range
     * @param point2 other value for range
     */
    public ComplexNumberRange(ComplexNumber point1, ComplexNumber point2) {
        point1_ = point1;
        point2_ = point2;
        extent_ = point2_.subtract(point1_);
    }

    /**
     * @return point1.
     */
    public ComplexNumber getPoint1() {
        return point1_;
    }

    /**
     * @return point2.
     */
    public ComplexNumber getPoint2() {
        return point2_;
    }

    /**
     * If params are outside 0, 1, then the interpolated point will be outside the range.
     *
     * @param realRatio      between 0 and 1 in real direction
     * @param imaginaryRatio between 0 and 1 in imaginary direction
     * @return interpolated position.
     */
    public ComplexNumber getInterpolatedPosition(double realRatio, double imaginaryRatio) {

        return new ComplexNumber(point1_.getReal() + extent_.getReal() * realRatio,
                point1_.getImaginary() + extent_.getImaginary() * imaginaryRatio);
    }

    public String toString() {
        return getPoint1() + " to " + this.getPoint2(); //NON-NLS
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComplexNumberRange that = (ComplexNumberRange) o;

        return !(point1_ != null ?
                !point1_.equals(that.point1_) :
                that.point1_ != null) && !(point2_ != null ? !point2_.equals(that.point2_) : that.point2_ != null);

    }

    @Override
    public int hashCode() {
        int result = point1_ != null ? point1_.hashCode() : 0;
        result = 31 * result + (point2_ != null ? point2_.hashCode() : 0);
        return result;
    }

}