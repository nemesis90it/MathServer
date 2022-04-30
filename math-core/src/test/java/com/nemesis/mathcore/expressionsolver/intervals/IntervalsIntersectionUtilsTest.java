package com.nemesis.mathcore.expressionsolver.intervals;

import com.nemesis.mathcore.expressionsolver.components.Constant;
import com.nemesis.mathcore.expressionsolver.intervals.model.*;
import com.nemesis.mathcore.expressionsolver.intervals.utils.IntervalsIntersectionUtils;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Point;
import junit.framework.TestCase;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_INTEGER;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.ONE;
import static java.math.BigDecimal.ZERO;


public class IntervalsIntersectionUtilsTest extends TestCase {

    public static final String var = "x";
    public static final String VOID_SET = "for no value of " + var;
    public static final N n = N.of(var);
    public static final Z z = Z.of(var);

    public void test_N_DoublePointInterval() {

        GenericInterval doublePointInterval;
        GenericInterval intersection;

        // CASE (1)

        doublePointInterval = new DoublePointInterval(var,
                new Delimiter(Delimiter.Type.CLOSED, -100),
                new Delimiter(Delimiter.Type.CLOSED, -10)
        );

        intersection = IntervalsIntersectionUtils.intersect(n, doublePointInterval);
        assertEquals(VOID_SET, intersection.toString());


        // CASE (2)

        doublePointInterval = new DoublePointInterval(var,
                new Delimiter(Delimiter.Type.CLOSED, -100),
                new Delimiter(Delimiter.Type.CLOSED, BigDecimal.valueOf(10.3))
        );

        intersection = IntervalsIntersectionUtils.intersect(n, doublePointInterval);
        assertEquals("0 <= x <= 10 , x ∈ ℕ", intersection.toString());


        // CASE (3)

        doublePointInterval = new DoublePointInterval(var,
                new Delimiter(Delimiter.Type.CLOSED, BigDecimal.valueOf(2.5)),
                new Delimiter(Delimiter.Type.CLOSED, BigDecimal.valueOf(10.7))
        );

        intersection = IntervalsIntersectionUtils.intersect(n, doublePointInterval);
        assertEquals("3 <= x <= 10 , x ∈ ℕ", intersection.toString());


    }

    public void test_N_SinglePointInterval() {

        SinglePointInterval singlePointInterval;
        GenericInterval intersection;

        // CASE (1a)

        singlePointInterval = new SinglePointInterval(var,
                new Point(new Constant(MINUS_ONE_INTEGER)), SinglePointInterval.Type.EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(n, singlePointInterval);
        assertEquals(VOID_SET, intersection.toString());

        // CASE (1b)

        singlePointInterval = new SinglePointInterval(var,
                new Point(new Constant(MINUS_ONE_INTEGER)), SinglePointInterval.Type.NOT_EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(n, singlePointInterval);
        assertEquals(n.toString(), intersection.toString());

        // CASE (2a)

        singlePointInterval = new SinglePointInterval(var,
                new Point(new Constant(ONE)), SinglePointInterval.Type.NOT_EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(n, singlePointInterval);
        assertEquals("x ∈ ℕ ∩ x ≠ 1", intersection.toString());

        // CASE (2b)

        singlePointInterval = new SinglePointInterval(var,
                new Point(new Constant(ONE)), SinglePointInterval.Type.EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(n, singlePointInterval);
        assertEquals("x = 1", intersection.toString());

        // CASE (2c)

        singlePointInterval = new SinglePointInterval(var,
                new Point(new Constant(BigDecimal.valueOf(1.1))), SinglePointInterval.Type.NOT_EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(n, singlePointInterval);
        assertEquals(n.toString(), intersection.toString());

        // CASE (2d)

        singlePointInterval = new SinglePointInterval(var,
                new Point(new Constant(BigDecimal.valueOf(1.1))), SinglePointInterval.Type.EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(n, singlePointInterval);
        assertEquals(VOID_SET, intersection.toString());

    }

    public void test_Z_DoublePointInterval() {

        GenericInterval doublePointInterval;
        GenericInterval intersection;

        doublePointInterval = new DoublePointInterval(var,
                new Delimiter(Delimiter.Type.CLOSED, -100),
                new Delimiter(Delimiter.Type.CLOSED, -10)
        );

        intersection = IntervalsIntersectionUtils.intersect(z, doublePointInterval);
        assertEquals("-100 <= x <= -10 , x ∈ Z", intersection.toString());


        doublePointInterval = new DoublePointInterval(var,
                new Delimiter(Delimiter.Type.CLOSED, BigDecimal.valueOf(-10.5)),
                new Delimiter(Delimiter.Type.CLOSED, BigDecimal.valueOf(15.7))
        );

        intersection = IntervalsIntersectionUtils.intersect(z, doublePointInterval);
        assertEquals("-10 <= x <= 15 , x ∈ Z", intersection.toString());

    }

    public void test_Z_SinglePointInterval() {

        SinglePointInterval singlePointInterval;
        GenericInterval intersection;


        singlePointInterval = new SinglePointInterval(var,
                new Point(new Constant(MINUS_ONE_INTEGER)), SinglePointInterval.Type.NOT_EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(z, singlePointInterval);
        assertEquals("x ∈ ℤ ∩ x ≠ -1", intersection.toString());


        singlePointInterval = new SinglePointInterval(var,
                new Point(new Constant(MINUS_ONE_INTEGER)), SinglePointInterval.Type.EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(z, singlePointInterval);
        assertEquals("x = -1", intersection.toString());


        singlePointInterval = new SinglePointInterval(var,
                new Point(new Constant(BigDecimal.valueOf(-1.1))), SinglePointInterval.Type.NOT_EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(z, singlePointInterval);
        assertEquals(z.toString(), intersection.toString());


        singlePointInterval = new SinglePointInterval(var,
                new Point(new Constant(BigDecimal.valueOf(-1.1))), SinglePointInterval.Type.EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(z, singlePointInterval);
        assertEquals(VOID_SET, intersection.toString());

    }

    public void test_SinglePointInterval() {

        SinglePointInterval a = new SinglePointInterval(var, new Point(new Constant(ONE)), SinglePointInterval.Type.EQUALS);
        SinglePointInterval b = new SinglePointInterval(var, new Point(new Constant(ZERO)), SinglePointInterval.Type.NOT_EQUALS);
        SinglePointInterval c = new SinglePointInterval(var, new Point(new Constant(ZERO)), SinglePointInterval.Type.EQUALS);
        SinglePointInterval d = new SinglePointInterval(var, new Point(new Constant(ONE)), SinglePointInterval.Type.NOT_EQUALS);
        GenericInterval intersection;

        intersection = IntervalsIntersectionUtils.intersect(a, a);
        assertEquals("x = 1", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("x = 1", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(a, c);
        assertEquals("for no value of x", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(a, d);
        assertEquals("for no value of x", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(b, b);
        assertEquals("x ≠ 0", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(b, c);
        assertEquals("for no value of x", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(b, d);
        assertEquals("x < 0 ∪ 0 < x < 1 ∪ x > 1", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(c, d);
        assertEquals("x = 0", intersection.toString());

    }

    public void test_DoublePointInterval() {
        // TODO
    }

    public void test_DoublePointInterval_SinglePointInterval() {
        // TODO
    }


}
