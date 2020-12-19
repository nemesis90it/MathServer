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
package com.numericalmethod.suanshu.stats.regression.linear.glm.distribution.link;

import com.numericalmethod.suanshu.stats.regression.linear.glm.GeneralizedLinearModel;

/**
 * This interface represents a link function <i>g(x)</i> in the Generalized Linear Model (GLM).
 *
 * <p>
 * The R equivalent function is {@code make.link}.
 *
 * @author Ken Yiu
 *
 * @see GeneralizedLinearModel
 */
public interface LinkFunction {

    /**
     * Inverse of the link function, i.e., <code>g<sup>-1</sup>(x)</code>.
     *
     * @param x <i>x</i>
     * @return <code>g<sup>-1</sup>(x)</code>
     */
    public double inverse(double x);

    /**
     * Derivative of the link function, i.e., <code>g'(x)</code>.
     *
     * @param x <i>x</i>
     * @return <code>g'(x)</code>
     */
    public double derivative(double x);
}
