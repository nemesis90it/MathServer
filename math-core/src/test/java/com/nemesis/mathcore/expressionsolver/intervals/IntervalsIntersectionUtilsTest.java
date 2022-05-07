package com.nemesis.mathcore.expressionsolver.intervals;

import com.nemesis.mathcore.expressionsolver.components.Constant;
import com.nemesis.mathcore.expressionsolver.intervals.model.*;
import com.nemesis.mathcore.expressionsolver.intervals.utils.IntervalsIntersectionUtils;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Point;
import junit.framework.TestCase;

import static com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter.Type.CLOSED;
import static com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter.Type.OPEN;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_INTEGER;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.ONE;
import static java.math.BigDecimal.ZERO;


public class IntervalsIntersectionUtilsTest extends TestCase {

    public static final String var = "x";
    public static final String VOID_SET = var + " ∈ ∅";
    public static final N n = N.of(var);
    public static final Z z = Z.of(var);

    public void test_N_DoublePointInterval() {

        GenericInterval doublePointInterval;
        GenericInterval intersection;

        // CASE (1)

        doublePointInterval = new DoublePointInterval(var,
                new Delimiter(CLOSED, -100),
                new Delimiter(CLOSED, -10)
        );

        intersection = IntervalsIntersectionUtils.intersect(n, doublePointInterval);
        assertEquals(VOID_SET, intersection.toString());


        // CASE (2)

        doublePointInterval = new DoublePointInterval(var,
                new Delimiter(CLOSED, -100),
                new Delimiter(CLOSED, 10.3)
        );

        intersection = IntervalsIntersectionUtils.intersect(n, doublePointInterval);
        assertEquals("0 ≤ x ≤ 10 , x ∈ ℕ", intersection.toString());


        // CASE (3)

        doublePointInterval = new DoublePointInterval(var,
                new Delimiter(CLOSED, 2.5),
                new Delimiter(CLOSED, 10.7)
        );

        intersection = IntervalsIntersectionUtils.intersect(n, doublePointInterval);
        assertEquals("3 ≤ x ≤ 10 , x ∈ ℕ", intersection.toString());


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
                new Point(new Constant(1.1)), SinglePointInterval.Type.NOT_EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(n, singlePointInterval);
        assertEquals(n.toString(), intersection.toString());

        // CASE (2d)

        singlePointInterval = new SinglePointInterval(var,
                new Point(new Constant(1.1)), SinglePointInterval.Type.EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(n, singlePointInterval);
        assertEquals(VOID_SET, intersection.toString());

    }

    public void test_Z_DoublePointInterval() {

        GenericInterval doublePointInterval;
        GenericInterval intersection;

        doublePointInterval = new DoublePointInterval(var,
                new Delimiter(CLOSED, -100),
                new Delimiter(CLOSED, -10)
        );

        intersection = IntervalsIntersectionUtils.intersect(z, doublePointInterval);
        assertEquals("-100 ≤ x ≤ -10 , x ∈ ℤ", intersection.toString());


        doublePointInterval = new DoublePointInterval(var,
                new Delimiter(CLOSED, -10.5),
                new Delimiter(CLOSED, 15.7)
        );

        intersection = IntervalsIntersectionUtils.intersect(z, doublePointInterval);
        assertEquals("-10 ≤ x ≤ 15 , x ∈ ℤ", intersection.toString());

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
                new Point(new Constant(-1.1)), SinglePointInterval.Type.NOT_EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(z, singlePointInterval);
        assertEquals(z.toString(), intersection.toString());


        singlePointInterval = new SinglePointInterval(var,
                new Point(new Constant(-1.1)), SinglePointInterval.Type.EQUALS);

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
        assertEquals("x ∈ ∅", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(a, d);
        assertEquals("x ∈ ∅", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(b, b);
        assertEquals("x ≠ 0", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(b, c);
        assertEquals("x ∈ ∅", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(b, d);
        assertEquals("x < 0 ∪ 0 < x < 1 ∪ x > 1", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(c, d);
        assertEquals("x = 0", intersection.toString());

    }

    public void test_DoublePointInterval() {

        DoublePointInterval a;
        DoublePointInterval b;
        GenericInterval intersection;


        // Disjoint intervals
        a = new DoublePointInterval(var, Delimiter.MINUS_INFINITY, new Delimiter(CLOSED, 1));
        b = new DoublePointInterval(var, new Delimiter(CLOSED, 2), Delimiter.PLUS_INFINITY);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals(VOID_SET, intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(b, a);
        assertEquals(VOID_SET, intersection.toString());

        //  CASE (1a) and (1b): x ≤ 1 ∩ x ≥ 1   -->  x = 1
        a = new DoublePointInterval(var, Delimiter.MINUS_INFINITY, new Delimiter(CLOSED, 1));
        b = new DoublePointInterval(var, new Delimiter(CLOSED, 1), Delimiter.PLUS_INFINITY);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("x = 1", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(b, a);
        assertEquals("x = 1", intersection.toString());

        //  CASE (2a): x ≤ 10, x ∈ ℕ  ∩  x ≥ 2.2   -->  3 ≤ x ≤ 10, x ∈ ℕ
        a = new SubSetN(var, Delimiter.CLOSED_ZERO, new Delimiter(CLOSED, 10));
        b = new DoublePointInterval(var, new Delimiter(CLOSED, 2.2), Delimiter.PLUS_INFINITY);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("3 ≤ x ≤ 10 , x ∈ ℕ", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(b, a);
        assertEquals("3 ≤ x ≤ 10 , x ∈ ℕ", intersection.toString());

        //  CASE (2b): x ≤ 10, x ∈ ℤ  ∩  x ≥ -2.2   -->  -2 ≤ x ≤ 10, x ∈ ℤ
        a = new SubSetZ(var, Delimiter.MINUS_INFINITY, new Delimiter(CLOSED, 10));
        b = new DoublePointInterval(var, new Delimiter(CLOSED, -2.2), Delimiter.PLUS_INFINITY);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("-2 ≤ x ≤ 10 , x ∈ ℤ", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(b, a);
        assertEquals("-2 ≤ x ≤ 10 , x ∈ ℤ", intersection.toString());

        // CASEs (3)

        a = new DoublePointInterval(var, new Delimiter(OPEN, -2), Delimiter.PLUS_INFINITY);
        b = new DoublePointInterval(var, new Delimiter(CLOSED, 5), Delimiter.PLUS_INFINITY);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("x ≥ 5", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(b, a);
        assertEquals("x ≥ 5", intersection.toString());

        a = new DoublePointInterval(var, Delimiter.MINUS_INFINITY, Delimiter.OPEN_ZERO);
        b = new DoublePointInterval(var, Delimiter.MINUS_INFINITY, new Delimiter(CLOSED, 5));

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("x < 0", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(b, a);
        assertEquals("x < 0", intersection.toString());

        a = new DoublePointInterval(var, new Delimiter(OPEN, -2), Delimiter.PLUS_INFINITY);
        b = new DoublePointInterval(var, Delimiter.MINUS_INFINITY, new Delimiter(CLOSED, 5));

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("-2 < x ≤ 5", intersection.toString());

        intersection = IntervalsIntersectionUtils.intersect(b, a);
        assertEquals("-2 < x ≤ 5", intersection.toString());

    }

    public void test_DoublePointInterval_SinglePointInterval() {

        /*
             - CASE (1a):  al < x < b  ∪  b < x < ar , x ∈ ℤ  (or x ∈ ℕ)
                       'a' contains the point of 'b'
                       'a' is a SubSetZ (or SubSetN)
                       'b' is integer and is of type NOT_EQUALS:
        */

        DoublePointInterval a;
        SinglePointInterval b;
        GenericInterval intersection;

        a = new SubSetZ(var, new Delimiter(OPEN, -2), Delimiter.PLUS_INFINITY);
        b = new SinglePointInterval(var, new Point(new Constant(ONE)), SinglePointInterval.Type.NOT_EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("-2 < x < 1 , x ∈ ℤ ∪ x > 1 , x ∈ ℤ", intersection.toString());

        a = new SubSetN(var, new Delimiter(OPEN, 2), Delimiter.PLUS_INFINITY);
        b = new SinglePointInterval(var, new Point(new Constant(3)), SinglePointInterval.Type.NOT_EQUALS);

        // TODO: manage case: 2 < x < 3 , x ∈ ℕ (or ℤ)  -->  is a void set
        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("2 < x < 3 , x ∈ ℕ ∪ x > 3 , x ∈ ℕ", intersection.toString());


        /*
             - CASE (1b): intersection is 'b'
                'a' contains the point of 'b'
                'a' is a SubSetZ (or SubSetN)
                'b' is integer and is of type EQUALS
        */

        a = new SubSetZ(var, new Delimiter(OPEN, -2), Delimiter.PLUS_INFINITY);
        b = new SinglePointInterval(var, new Point(new Constant(ONE)), SinglePointInterval.Type.EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("x = 1", intersection.toString());

        a = new SubSetN(var, new Delimiter(OPEN, 2), Delimiter.PLUS_INFINITY);
        b = new SinglePointInterval(var, new Point(new Constant(10)), SinglePointInterval.Type.EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("x = 10", intersection.toString());


        /*
             - CASE (1c): intersection is 'a'
                'a' contains the point of 'b'
                'a' is a SubSetZ (or SubSetN)
                'b' is not integer and is of type NOT_EQUALS
        */

        a = new SubSetZ(var, new Delimiter(OPEN, -2), Delimiter.PLUS_INFINITY);
        b = new SinglePointInterval(var, new Point(new Constant(-1.1)), SinglePointInterval.Type.NOT_EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("x > -2 , x ∈ ℤ", intersection.toString());

        a = new SubSetN(var, new Delimiter(OPEN, 2), Delimiter.PLUS_INFINITY);
        b = new SinglePointInterval(var, new Point(new Constant(2.1)), SinglePointInterval.Type.NOT_EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("x > 2 , x ∈ ℕ", intersection.toString());


        /*
             - CASE (1d): intersection is void
                'a' contains the point of 'b'
                'a' is a SubSetZ (or SubSetN)
                'b' is not integer and is of type EQUALS
        */

        a = new SubSetZ(var, new Delimiter(OPEN, -2), Delimiter.PLUS_INFINITY);
        b = new SinglePointInterval(var, new Point(new Constant(-1.1)), SinglePointInterval.Type.EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals(VOID_SET, intersection.toString());

        a = new SubSetN(var, new Delimiter(OPEN, 2), Delimiter.PLUS_INFINITY);
        b = new SinglePointInterval(var, new Point(new Constant(2.1)), SinglePointInterval.Type.EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals(VOID_SET, intersection.toString());


        /*
             - CASE (2a): al < x < b  ∪  b < x < ar,  x ∈ ℝ
                'a' contains the point of 'b'
                'a' is a continuous DoublePointInterval
                'b' is of type NOT_EQUALS
        */

        a = new DoublePointInterval(var, new Delimiter(OPEN, -2), new Delimiter(CLOSED, 2));
        b = new SinglePointInterval(var, new Point(new Constant(1.5)), SinglePointInterval.Type.NOT_EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("-2 < x < 1.5 ∪ 1.5 < x ≤ 2", intersection.toString());


        /*
             - CASE (2b): intersection is 'b'
                'a' contains the point of 'b'
                'a' is a continuous DoublePointInterval
                'b' is of type EQUALS
        */

        a = new DoublePointInterval(var, new Delimiter(OPEN, -2), Delimiter.PLUS_INFINITY);
        b = new SinglePointInterval(var, new Point(new Constant(ONE)), SinglePointInterval.Type.EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("x = 1", intersection.toString());


        /*
             - CASE (3a): intersection is 'a'
                'a' not contains the point of 'b'
                'b' is of type NOT_EQUALS
        */

        a = new DoublePointInterval(var, new Delimiter(OPEN, -2), Delimiter.PLUS_INFINITY);
        b = new SinglePointInterval(var, new Point(new Constant(-10)), SinglePointInterval.Type.NOT_EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals("x > -2", intersection.toString());


        /*
             - CASE (3a): intersection is void
                'a' not contains the point of 'b'
                'b' is of type EQUALS
        */

        a = new DoublePointInterval(var, new Delimiter(OPEN, -2), Delimiter.PLUS_INFINITY);
        b = new SinglePointInterval(var, new Point(new Constant(-10)), SinglePointInterval.Type.EQUALS);

        intersection = IntervalsIntersectionUtils.intersect(a, b);
        assertEquals(VOID_SET, intersection.toString());

    }


}
