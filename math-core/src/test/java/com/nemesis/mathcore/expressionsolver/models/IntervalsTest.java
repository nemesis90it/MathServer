package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.intervals.model.DoublePointInterval;
import com.nemesis.mathcore.expressionsolver.intervals.model.GenericInterval;
import com.nemesis.mathcore.expressionsolver.intervals.model.N;
import com.nemesis.mathcore.expressionsolver.intervals.model.Z;
import com.nemesis.mathcore.expressionsolver.intervals.utils.IntervalsIntersectionUtils;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import junit.framework.TestCase;

import java.math.BigDecimal;


public class IntervalsTest extends TestCase {

    public void testIntersection() {
        GenericInterval a = new DoublePointInterval("x", new Delimiter(
                Delimiter.Type.CLOSED, 1),
                Delimiter.PLUS_INFINITY);

        GenericInterval b = new DoublePointInterval("x",
                new Delimiter(Delimiter.Type.CLOSED, 1),
                new Delimiter(Delimiter.Type.CLOSED, new BigDecimal("3.4")));


        GenericInterval intersectionWithN;

        intersectionWithN = IntervalsIntersectionUtils.intersect(N.of("x"), a);
        assertEquals("x >= 1 , x ∈ ℕ", intersectionWithN.toString());

        intersectionWithN = IntervalsIntersectionUtils.intersect(N.of("x"), b);
        assertEquals("1 <= x <= 3 , x ∈ ℕ", intersectionWithN.toString());


        GenericInterval intersectionWithZ;

        intersectionWithZ = IntervalsIntersectionUtils.intersect(Z.of("x"), a);
        assertEquals("x >= 1 , x ∈ Z", intersectionWithZ.toString());

        intersectionWithZ = IntervalsIntersectionUtils.intersect(Z.of("x"), b);
        assertEquals("1 <= x <= 3 , x ∈ Z", intersectionWithZ.toString());


    }


    public void testMapOfClasses() {


//        GenericInterval n = N.get("x");
//        GenericInterval p = new PositiveIntegerInterval("x", new Delimiter(Delimiter.Type.CLOSED, 1), Delimiter.PLUS_INFINITY);
//        GenericInterval d = new DoublePointInterval("x", new Delimiter(Delimiter.Type.CLOSED, 1), Delimiter.PLUS_INFINITY);
//
//        assertEquals("N,PositiveIntegerInterval", map.get(new IntervalInputTypes<>(n, p)).apply(n, p));
//        assertEquals("N,DoublePointInterval", map.get(new IntervalInputTypes<>(n, d)).apply(n, d));
//        assertEquals("N,N", map.get(new IntervalInputTypes<>(n, n)).apply(n, n));

    }


}
