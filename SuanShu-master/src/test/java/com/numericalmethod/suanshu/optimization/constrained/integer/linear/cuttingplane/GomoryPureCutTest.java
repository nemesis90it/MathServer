/*
 * Copyright (c) Numerical Method Inc.
 * http://www.numericalmethod.com/
 * 
 * THIS SOFTWARE IS LICENSED, NOT SOLD.
 * 
 * YOU MAY USE THIS SOFTWARE ONLY AS DESCRIBED IN THE LICENSE.
 * IF YOU ARE NOT AWARE OF AND/OR DO NOT AGREE TO THE TERMS OF THE LICENSE,
 * DO NOT USE THIS SOFTWARE.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITH NO WARRANTY WHATSOEVER,
 * EITHER EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION,
 * ANY WARRANTIES OF ACCURACY, ACCESSIBILITY, COMPLETENESS,
 * FITNESS FOR A PARTICULAR PURPOSE, MERCHANTABILITY, NON-INFRINGEMENT, 
 * TITLE AND USEFULNESS.
 * 
 * IN NO EVENT AND UNDER NO LEGAL THEORY,
 * WHETHER IN ACTION, CONTRACT, NEGLIGENCE, TORT, OR OTHERWISE,
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIMS, DAMAGES OR OTHER LIABILITIES,
 * ARISING AS A RESULT OF USING OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.numericalmethod.suanshu.optimization.constrained.integer.linear.cuttingplane;

import com.numericalmethod.suanshu.matrix.doubles.matrixtype.dense.DenseMatrix;
import com.numericalmethod.suanshu.optimization.constrained.constraint.linear.LinearLessThanConstraints;
import com.numericalmethod.suanshu.optimization.constrained.convex.sdp.socp.qp.lp.exception.LPInfeasible;
import com.numericalmethod.suanshu.optimization.constrained.integer.linear.problem.PureILPProblem;
import com.numericalmethod.suanshu.optimization.problem.MinimizationSolution;
import com.numericalmethod.suanshu.vector.doubles.Vector;
import com.numericalmethod.suanshu.vector.doubles.dense.DenseVector;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Haksun Li
 */
public class GomoryPureCutTest {

    /**
     * Toh's lecture note table 1, page 6
     */
    @Test
    public void test_0020() throws LPInfeasible, Exception {
        PureILPProblem problem = new PureILPProblem(
                new DenseVector(new double[]{-1, -1}), null,
                new LinearLessThanConstraints(new DenseMatrix(new double[][]{
                    {7, 1},
                    {-1, 1}
                }), new DenseVector(new double[]{15, 1})),
                null, null, 1e-8);

        GomoryPureCut instance = new GomoryPureCut();
        MinimizationSolution<Vector> soln = instance.solve(problem);
        assertEquals(-3, soln.minimum(), 0);
        assertArrayEquals(new double[]{2, 1}, soln.minimizer().toArray(), 0);
    }

    /**
     * lecture3.pdf, page 10
     */
    @Test
    public void test_0030() throws LPInfeasible, Exception {
        PureILPProblem problem = new PureILPProblem(
                new DenseVector(new double[]{-4, 1}),
                null,
                new LinearLessThanConstraints(new DenseMatrix(new double[][]{
                    {7, -2},
                    {0, 1},
                    {2, -2}
                }), new DenseVector(new double[]{14, 3, 3})),
                null, null, 1e-8);

        GomoryPureCut instance = new GomoryPureCut();
        MinimizationSolution<Vector> soln = instance.solve(problem);
        assertEquals(-7, soln.minimum(), 1e-14);
        assertArrayEquals(new double[]{2, 1}, soln.minimizer().toArray(), 1e-14);
    }

    /**
     * example 11.1 in Applied Programming: Modeling and Solution (Der-San Chen)
     */
    @Test
    public void test_0040() throws LPInfeasible, Exception {
        PureILPProblem problem = new PureILPProblem(
                new DenseVector(new double[]{-5, 2}),
                null,
                new LinearLessThanConstraints(new DenseMatrix(new double[][]{
                    {-1, 2},
                    {3, 2},
                    {-1, -3}
                }), new DenseVector(new double[]{5, 19, -9})),
                null, null, 1e-8);

        GomoryPureCut instance = new GomoryPureCut();
        MinimizationSolution<Vector> soln = instance.solve(problem);
        assertEquals(-21, soln.minimum(), 0);
        assertArrayEquals(new double[]{5, 2}, soln.minimizer().toArray(), 1e-14);
    }

    /**
     * gomoryExample.pdf, page 3
     */
    @Test
    public void test_0050() throws LPInfeasible, Exception {
        PureILPProblem problem = new PureILPProblem(
                new DenseVector(new double[]{2, 15, 18}),
                null,
                new LinearLessThanConstraints(new DenseMatrix(new double[][]{
                    {-1, 2, -6},
                    {0, 1, 2},
                    {2, 0, 10},
                    {-1, 1, 0}
                }), new DenseVector(new double[]{-10, 6, 19, -2})),
                null, null, 1e-8);

        GomoryPureCut instance = new GomoryPureCut();
        MinimizationSolution<Vector> soln = instance.solve(problem);
        assertEquals(26, soln.minimum(), 0);
        assertArrayEquals(new double[]{4, 0, 1}, soln.minimizer().toArray(), 0);
    }

    /**
     * 620362_Gomory.pdf, page 39
     */
    @Test
    public void test_0060() throws LPInfeasible, Exception {
        PureILPProblem problem = new PureILPProblem(
                new DenseVector(new double[]{-7, -9}),
                null,
                new LinearLessThanConstraints(new DenseMatrix(new double[][]{
                    {-1, 3},
                    {7, 1}
                }), new DenseVector(new double[]{6, 35})),
                null, null, 1e-8);

        GomoryPureCut instance = new GomoryPureCut();
        MinimizationSolution<Vector> soln = instance.solve(problem);
        assertEquals(-55, soln.minimum(), 0);
        assertArrayEquals(new double[]{4, 3}, soln.minimizer().toArray(), 0);
    }

    /**
     * gomoryEaxample.pdf, page 1
     */
    @Test
    public void test_0070() throws LPInfeasible, Exception {
        PureILPProblem problem = new PureILPProblem(
                new DenseVector(new double[]{-3, -4}),
                null,
                new LinearLessThanConstraints(new DenseMatrix(new double[][]{
                    {3, -1},
                    {3, 11}
                }), new DenseVector(new double[]{12, 66})),
                null, null, 1e-8);

        GomoryPureCut instance = new GomoryPureCut();
        MinimizationSolution<Vector> soln = instance.solve(problem);
        assertEquals(-31, soln.minimum(), 1e-14);
        assertArrayEquals(new double[]{5, 4}, soln.minimizer().toArray(), 1e-14);
    }

    /**
     * lect8.pdf, page 31
     */
    @Test
    public void test_0080() throws LPInfeasible, Exception {
        PureILPProblem problem = new PureILPProblem(
                new DenseVector(new double[]{-1, -27}),
                null,
                new LinearLessThanConstraints(new DenseMatrix(new double[][]{
                    {-1, 1},
                    {24, 4}
                }), new DenseVector(new double[]{1, 25})),
                null, null, 1e-8);

        GomoryPureCut instance = new GomoryPureCut();
        MinimizationSolution<Vector> soln = instance.solve(problem);
        assertEquals(-27, soln.minimum(), 1e-14);
        assertArrayEquals(new double[]{0, 1}, soln.minimizer().toArray(), 0);
    }
}
