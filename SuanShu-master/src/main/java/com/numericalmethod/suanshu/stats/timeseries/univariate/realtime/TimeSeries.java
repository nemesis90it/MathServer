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
package com.numericalmethod.suanshu.stats.timeseries.univariate.realtime;

/**
 * This is a univariate time series indexed by integers.
 * The entries are the pair: <i>{(n, double)}</i>.
 * This time series is often seen in classical time series analysis, where the data points are equally spaced.
 *
 * @author Haksun Li
 */
public interface TimeSeries extends com.numericalmethod.suanshu.stats.timeseries.univariate.TimeSeries<Integer, TimeSeries.Entry> {

    /**
     * This is the {@code TimeSeries.Entry} for an integer number -indexed univariate time series.
     */
    public static class Entry extends com.numericalmethod.suanshu.stats.timeseries.univariate.TimeSeries.Entry<Integer> {

        /**
         * Construct an instance of {@code TimeSeries.Entry}.
         *
         * @param time  the timestamp
         * @param value the entry value
         */
        public Entry(int time, double value) {
            super(time, value);
        }
    }

    /**
     * Get the value at time {@code t}.
     *
     * @param t a time index
     * @return the value at time {@code t}
     */
    public abstract double get(int t);
}
