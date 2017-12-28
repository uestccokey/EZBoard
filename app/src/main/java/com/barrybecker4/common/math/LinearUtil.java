///** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
//package com.barrybecker4.common.math;
//
//import com.barrybecker4.common.format.FormatUtil;
//
//import javax.vecmath.GMatrix;
//import javax.vecmath.GVector;
//import javax.vecmath.Vector2d;
//
///**
// * This class implements a number of static utility functions that are useful for math.
// * Mostly linear algebra matrix solvers and the like.
// *
// * @author Barry Becker
// */
//public final class LinearUtil {
//
//    private LinearUtil() {}
//
//    /**
//     * matrix conjugate-Gradient solver for Ax = b
//     * See http://en.wikipedia.org/wiki/Conjugate_gradient_method
//     *
//     * @param matrix the matrix of linear coefficients
//     * @param b the right hand side
//     * @param initialGuess the initial guess for the solution x, x0
//     * @param eps the tolerable error (eg .0000001)
//     */
//    public static GVector conjugateGradientSolve( GMatrix matrix, GVector b,
//                                                  GVector initialGuess, double eps ) {
//
//        ConjugateGradientSolver solver = new ConjugateGradientSolver(matrix, b);
//        solver.setEpsilon(eps);
//        return solver.solve(initialGuess);
//    }
//
//    /**
//     * Pretty print the matrix for debugging.
//     */
//    public static void printMatrix( GMatrix matrix ) {
//        for ( int i = 0; i < matrix.getNumRow(); i++ ) {
//            for ( int j = 0; j < matrix.getNumCol(); j++ ) {
//                double a = matrix.getElement( i, j );
//                if ( a == 0 )
//                    System.out.print( "  0  " );
//                else
//                    System.out.print( FormatUtil.formatNumber(a) + ' ' );
//            }
//            System.out.println();
//        }
//    }
//
//    /**
//     * @return the distance between two points
//     */
//    public static double distance(Vector2d p1, Vector2d p2) {
//        double dx = p2.x - p1.x;
//        double dy = p2.y - p1.y;
//        return Math.sqrt(dx * dx + dy * dy);
//    }
//
//    /**
//     * Vectors are considered approximately equal if x and y components are within eps of each other.
//     * @return true if approximately equal.
//     */
//    public static boolean appxVectorsEqual(Vector2d vec1, Vector2d vec2, double eps) {
//        return (Math.abs(vec1.x - vec2.x) < eps && Math.abs(vec1.y - vec2.y) < eps);
//    }
//
//    /**
//     * Vectors are considered approximately equal if x and y components are within eps of each other.
//     * @return true if approximately equal.
//     */
//    public static boolean appxVectorsEqual(GVector vec1, GVector vec2, double eps) {
//        assert vec1.getSize() == vec2.getSize();
//        double totalDiff = 0;
//        for (int i = 0; i < vec1.getSize(); i++) {
//            double diff = vec2.getElement(i) - vec1.getElement(i);
//            totalDiff += (diff * diff);
//        }
//        return (Math.sqrt(totalDiff) < eps);
//    }
//}
//
