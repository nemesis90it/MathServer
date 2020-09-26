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
package com.numericalmethod.suanshu.analysis.differentiation.differentiability;

import com.numericalmethod.suanshu.analysis.function.matrix.RntoMatrix;

/**
 * A function, <i>f</i>, is said to be of class C<sup>2</sup> if the first and second derivatives, <i>f'</i> and <i>f''</i>, exist.
 *
 * @author Haksun Li
 * @see <a href="http://en.wikipedia.org/wiki/Smooth_function">Wikipedia: Smooth function</a>
 */
public interface C2 extends C1 {

    /**
     * Get the Hessian matrix function, <i>H</i>, of a real valued function <i>f</i>.
     *
     * @return the Hessian matrix function
     */
    public RntoMatrix H();
}
