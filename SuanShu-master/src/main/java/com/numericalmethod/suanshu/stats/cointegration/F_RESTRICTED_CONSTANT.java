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
package com.numericalmethod.suanshu.stats.cointegration;

import com.numericalmethod.suanshu.stats.stochasticprocess.univariate.Realization;
import com.numericalmethod.suanshu.stats.stochasticprocess.univariate.integration.Filtration;
import com.numericalmethod.suanshu.stats.stochasticprocess.univariate.integration.FiltrationFunction;

/**
 * F = [B_dr', 1]'
 *
 * @author Ken Yiu
 */
class F_RESTRICTED_CONSTANT implements JohansenAsymptoticDistribution.F {

    @Override
    public FiltrationFunction[] evaluate(Realization[] B) {
        final int dim = B.length + 1;

        FiltrationFunction[] F = new FiltrationFunction[dim];
        for (int i = 0; i < dim - 1; ++i) {
            final Filtration B_i = new Filtration(B[i]);
            F[i] = new FiltrationFunction() {

                @Override
                public double evaluate(int t) {
                    double F = B_i.B(t);
                    return F;
                }
            };
        }
        F[dim - 1] = new FiltrationFunction() {

            @Override
            public double evaluate(int t) {
                return 1.;
            }
        };

        return F;
    }
}
