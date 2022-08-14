package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.intervals.model.DoublePointInterval;
import com.nemesis.mathcore.expressionsolver.intervals.model.NoPointInterval;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import junit.framework.TestCase;

import static com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter.MINUS_INFINITY;
import static com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter.PLUS_INFINITY;

public class DomainTest extends TestCase {

    private static final String VAR = "x";


    /*

    -----000000000|-----------------00000000000000-------
    (x<0)             (1 ≤ x < 3)           (x>5)

    000000000000000000000000-----------------------------
                            (x>2)

    Result

    000000000000000000000000--------0000000000000------
                             (x>2 ∪ x<3)          (x>5)

     */
    public void testDomains() {
        Domain domain = new Domain();
        domain.unionWith(new DoublePointInterval(VAR,
                MINUS_INFINITY,
                new Delimiter(Delimiter.Type.OPEN, 0)
        ));
        assertEquals("x < 0 , x ∈ ℝ", domain.toString());

        domain.unionWith(new DoublePointInterval(VAR,
                new Delimiter(Delimiter.Type.CLOSED, 1),
                new Delimiter(Delimiter.Type.OPEN, 3)
        ));
        assertEquals("x < 0 , x ∈ ℝ ∪ 1 ≤ x < 3 , x ∈ ℝ", domain.toString());

        domain.unionWith(new DoublePointInterval(VAR,
                new Delimiter(Delimiter.Type.OPEN, 5),
                PLUS_INFINITY
        ));
        assertEquals("x < 0 , x ∈ ℝ ∪ 1 ≤ x < 3 , x ∈ ℝ ∪ x > 5 , x ∈ ℝ", domain.toString());

        domain.intersectWith(new DoublePointInterval(VAR,
                new Delimiter(Delimiter.Type.OPEN, 2),
                PLUS_INFINITY
        ));
        assertEquals("2 < x < 3 , x ∈ ℝ ∪ x > 5 , x ∈ ℝ", domain.toString());

        domain.intersectWith(new NoPointInterval(VAR));
        assertEquals("x ∈ ∅", domain.toString());

    }

}
