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
package com.numericalmethod.suanshu.stats.stochasticprocess.multivariate.sde.coefficients;

import com.numericalmethod.suanshu.stats.stochasticprocess.multivariate.sde.Ft;
import com.numericalmethod.suanshu.matrix.doubles.Matrix;
import com.numericalmethod.suanshu.stats.stochasticprocess.multivariate.sde.FtAdaptedRealFunction;

/**
 * The class represents a constant diffusion coefficient function.
 *
 * @deprecated This implementation is slow. Use {@link ConstantSigma1} instead.
 *
 * @author Haksun Li
 */
@Deprecated
public class ConstantSigma2 extends Sigma {

    /**
     * the constant diffusion matrix
     */
    private final Matrix sigma;

    /**
     * Construct a constant diffusion coefficient function.
     *
     * @param sigma the constant diffusion matrix
     */
    public ConstantSigma2(Matrix sigma) {
        this.sigma = sigma;
    }

    @Override
    public FtAdaptedRealFunction sigma_i_j(final int i, final int j) {
        return new FtAdaptedRealFunction() {

            public double evaluate(Ft ft) {
                return sigma.get(i, j);
            }
        };
    }

    public int nRows() {
        return sigma.nRows();
    }

    public int nCols() {
        return sigma.nCols();
    }
}
