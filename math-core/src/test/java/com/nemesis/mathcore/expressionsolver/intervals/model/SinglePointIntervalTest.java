package com.nemesis.mathcore.expressionsolver.intervals.model;

import com.nemesis.mathcore.expressionsolver.components.Constant;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Point;
import junit.framework.TestCase;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.intervals.model.SinglePointInterval.Type.EQUALS;
import static com.nemesis.mathcore.expressionsolver.intervals.model.SinglePointInterval.Type.NOT_EQUALS;

public class SinglePointIntervalTest extends TestCase {

    public void testCompareTo() {

        SinglePointInterval a = new SinglePointInterval("x", new Point(new Constant(BigDecimal.ZERO)), NOT_EQUALS);
        SinglePointInterval b = new SinglePointInterval("x", new Point(new Constant(BigDecimal.ONE)), EQUALS);

        assertEquals(1, a.compareTo(b)); // a > b (minus infinity precedes any number)
        assertEquals(-1, b.compareTo(a)); // b < a (any number succeeds minus infinity)

    }
}