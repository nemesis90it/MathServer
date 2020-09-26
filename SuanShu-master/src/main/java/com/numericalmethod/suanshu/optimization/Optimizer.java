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
package com.numericalmethod.suanshu.optimization;

import com.numericalmethod.suanshu.optimization.problem.OptimProblem;

/**
 * Optimization, or mathematical programming, refers to choosing the best element from some set of available alternatives.
 * In the simplest case,
 * this means solving problems in which one seeks to minimize (or maximize) a real function by systematically choosing the values of real or integer variables from within an allowed set.
 * The generalization of optimization theory and techniques to other formulations comprises a large area of applied mathematics.
 * More generally, it means finding "best available" values of some objective function given a defined domain,
 * including a variety of different types of objective functions and different types of domains.
 * <p/>
 * This interface defines the input (the optimization problem) and output (the optimization solution) of an optimization algorithm.
 *
 * @param <P> the optimization problem type
 * @param <S> the optimization solution type
 * @author Haksun Li
 * @see <a href="http://en.wikipedia.org/wiki/Mathematical_programming">Wikipedia: Mathematical optimization</a>
 */
public interface Optimizer<P, S> {

    /**
     * Solve an optimization problem, e.g., {@link OptimProblem}.
     *
     * @param problem an optimization problem
     * @return a solution to the optimization problem
     * @throws Exception when there is an error solving the problem
     */
    public S solve(P problem) throws Exception;
}
