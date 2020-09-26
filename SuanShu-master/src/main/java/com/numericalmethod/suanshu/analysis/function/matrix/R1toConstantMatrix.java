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
package com.numericalmethod.suanshu.analysis.function.matrix;

import com.numericalmethod.suanshu.matrix.doubles.ImmutableMatrix;
import com.numericalmethod.suanshu.matrix.doubles.Matrix;

/**
 * A constant matrix function maps a real number to a constant matrix: \(R^n \rightarrow A\).
 *
 * @author Haksun Li
 */
public class R1toConstantMatrix extends R1toMatrix {

    private final ImmutableMatrix A;

    /**
     * Construct a constant matrix function.
     *
     * @param A the constant matrix
     */
    public R1toConstantMatrix(Matrix A) {
        this.A = new ImmutableMatrix(A);
    }

    @Override
    public ImmutableMatrix evaluate(double x) {
        return A;
    }
}
