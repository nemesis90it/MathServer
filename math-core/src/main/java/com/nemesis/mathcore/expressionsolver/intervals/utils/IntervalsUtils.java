package com.nemesis.mathcore.expressionsolver.intervals.utils;

import com.nemesis.mathcore.expressionsolver.components.Infinity;
import com.nemesis.mathcore.expressionsolver.exception.DisjointIntervalsException;
import com.nemesis.mathcore.expressionsolver.exception.VariablesMismatchException;
import com.nemesis.mathcore.expressionsolver.intervals.model.*;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;
import java.util.Objects;

/* Intervals summary:

Double Point Intervals:
    O-------------O     STRICTLY_BETWEEN          a<x<b
    |-------------|     BETWEEN                   a<=x<=b
    |-------------O     RIGHT_STRICTLY_BETWEEN    a<=x<b
    O-------------|     LEFT_STRICTLY_BETWEEN     a<x<=b
    |-------------      GREATER_THAN_OR_EQUALS    x>=a
    O-------------      GREATER_THAN              x>a
    --------------|     LESS_THAN_OR_EQUALS       x<=a
    --------------O     LESS_THAN                 x<a
    ---------------     -                         for each x

Single point intervals
    OOOOOOO|OOOOOOO     -                         x=a
    -------O-------     -                         x!=a

No Point Itervals
    OOOOOOOOOOOOOOO     -                         for no x

 */

public class IntervalsUtils {

    public static GenericInterval intersect(GenericInterval a, GenericInterval b) {

        if (!Objects.equals(a.getVariable(), b.getVariable())) {
            throw new VariablesMismatchException(String.format("Cannot intersect intervals on different variables [%s] and [%s]", a, b));
        }

        if (a instanceof NoPointInterval || b instanceof NoPointInterval || IntervalsUtils.areDisjoint(a, b)) {
            return new NoPointInterval(a.getVariable());
        }

        if (a instanceof GenericIntersection || b instanceof GenericIntersection) {
            return new GenericIntersection(a, b);
        }

        return IntervalsIntersectionUtils.intersect(a, b);

    }

    public static GenericInterval merge(GenericInterval a, GenericInterval b) throws DisjointIntervalsException {

        if (!Objects.equals(a.getVariable(), b.getVariable())) {
            throw new VariablesMismatchException(String.format("Cannot intersect intervals on different variables [%s] and [%s]", a, b));
        }

        if (areDisjoint(a, b)) {
            throw new DisjointIntervalsException("Cannot merge disjoint intervals");
        }

        if (a instanceof DoublePointInterval aDpi && b instanceof DoublePointInterval bDpi) {
            final int leftDistance = bDpi.getLeftDelimiter().getComponent().compareTo(aDpi.getLeftDelimiter().getComponent());
            final Delimiter leftDelimiter = leftDistance > 0 ? aDpi.getLeftDelimiter() : bDpi.getLeftDelimiter();

            final int rightDistance = aDpi.getRightDelimiter().getComponent().compareTo(bDpi.getRightDelimiter().getComponent());
            final Delimiter rightDelimiter = rightDistance > 0 ? bDpi.getRightDelimiter() : aDpi.getRightDelimiter();

            // TODO: left distance or right distance = 0 ? Check delimiters type (open/closed)

            return new DoublePointInterval(a.getVariable(), leftDelimiter, rightDelimiter);
        } else {
            throw new UnsupportedOperationException("Not implemented yet"); // TODO
        }

    }

    public static boolean areDisjoint(GenericInterval a, GenericInterval b) {

        if (!Objects.equals(a.getVariable(), b.getVariable())) {
            throw new VariablesMismatchException(String.format("Cannot intersect intervals on different variables [%s] and [%s]", a, b));
        }

        if (a instanceof Z && b instanceof Z) {
            return false;
        } else if (a instanceof NoPointInterval || b instanceof NoPointInterval) {
            return true;
        } else if (a instanceof Z z && b instanceof DoublePointInterval dpi) {
            return areDisjoint(z, dpi);
        } else if (a instanceof DoublePointInterval dpi && b instanceof Z z) {
            return areDisjoint(z, dpi);
        } else if (a instanceof DoublePointInterval dpi1 && b instanceof DoublePointInterval dpi2) {
            return areDisjoint(dpi1, dpi2);
        } else if (a instanceof SinglePointInterval spi1 && b instanceof SinglePointInterval spi2) {
            return !spi1.getPoint().equals(spi2.getPoint());
        } else if (a instanceof DoublePointInterval dpi && b instanceof SinglePointInterval spi) {
            return areDisjoint(dpi, spi);
        } else if (a instanceof SinglePointInterval spi && b instanceof DoublePointInterval dpi) {
            return areDisjoint(dpi, spi);
        } else {
            throw new UnsupportedOperationException("Not implemented yet"); // Should never happen (not managed cases)
        }
    }

    private static boolean areDisjoint(DoublePointInterval a, DoublePointInterval b) {
        /*
              a          b
           |-----|    |-----|
           al    ar  bl     br

              b          a
           |-----|    |-----|
           bl    br  al     ar

              a      b
           |-----O-----|
           al  ar=bl   br
           ar isOpen AND bl isOpen

              b      a
           |-----O-----|
           bl  br=al   ar
           br isOpen AND al isOpen

        */
        Delimiter al = a.getLeftDelimiter();
        Delimiter ar = a.getRightDelimiter();
        Delimiter bl = b.getLeftDelimiter();
        Delimiter br = b.getRightDelimiter();

        return bl.getComponent().compareTo(ar.getComponent()) > 0 ||
                al.getComponent().compareTo(br.getComponent()) > 0 ||
                (ar.getComponent().compareTo(bl.getComponent()) == 0 && ar.isOpen() && bl.isOpen()) ||
                (br.getComponent().compareTo(al.getComponent()) == 0 && br.isOpen() && al.isOpen());
    }

    private static boolean areDisjoint(Z a, DoublePointInterval b) {

        /*
                    |           |
                   n1          n2
        CASE 1:
                   O-----------O
                  n1=bl      br=n2

        CASE 2:
                   O---------⊕ |
                  n1=bl    br<n2

        CASE 3:
                   | ⊕-------⊕ |
                 n1>bl    br<n2

        CASE 4:
                   | ⊕-------O
                 n1>bl     br=n2

        Example:
	            Case 1:  2   <  x  <  3    	  bl isInteger && bl isOpen && br isInteger && br isOpen && width=1
	            Case 2:  2   <  x  <= 2,*     bl isInteger && bl isOpen && && sameIntPart
	            Case 3:  2,* <= x  <= 2,*     sameIntPart && width<1
	            Case 4:  2,* <= x  <  3       br isOpen && br isInteger && width<1
         */

        final Delimiter bl = b.getLeftDelimiter();
        final Delimiter br = b.getRightDelimiter();

        if (bl.getComponent() instanceof Infinity || br.getComponent() instanceof Infinity) {
            return false;
        }

        final BigDecimal blValue = bl.getComponent().getValue();
        final BigDecimal brValue = br.getComponent().getValue();

        final BigDecimal width = blValue.subtract(brValue).abs();
        final boolean bDelimitersHaveSameIntPart = blValue.toBigInteger().compareTo(brValue.toBigInteger()) == 0;

        final boolean areDisjointCase1 = width.compareTo(BigDecimal.ONE) == 0 &&
                bl.isOpen() && MathUtils.isIntegerValue(blValue) &&
                br.isOpen() && MathUtils.isIntegerValue(brValue);

        final boolean areDisjointCase2 = bl.isOpen() && MathUtils.isIntegerValue(blValue) && bDelimitersHaveSameIntPart;

        final boolean areDisjointCase3 = width.compareTo(BigDecimal.ONE) < 1 && bDelimitersHaveSameIntPart;

        final boolean areDisjointCase4 = width.compareTo(BigDecimal.ONE) < 1 && br.isOpen() && MathUtils.isIntegerValue(brValue);

        return areDisjointCase1 || areDisjointCase2 || areDisjointCase3 || areDisjointCase4;

    }

    private static boolean areDisjoint(DoublePointInterval a, SinglePointInterval b) {
         /*
                   a
           |    |-----|
           b   al     ar

            b   a
            |0-----|
           b=al     ar

              a
           |-----|    |
           al    ar   b

              a   b
           |-----0|
           al   ar=b

        */

        int leftDistance = a.getLeftDelimiter().getComponent().compareTo(b.getPoint().getComponent());
        if (leftDistance >= 0) {
            return leftDistance > 0 || a.getLeftDelimiter().isOpen();
        }

        int rightDistance = b.getPoint().getComponent().compareTo(a.getRightDelimiter().getComponent());
        if (rightDistance >= 0) {
            return rightDistance > 0 || a.getRightDelimiter().isOpen();
        }

        return false;
    }

    public static boolean areAdjacent(GenericInterval a, GenericInterval b) { // TODO

        return switch (a) {
            case DoublePointInterval d -> {
                switch (b) {
                    case DoublePointInterval d1 -> {
                        yield areAdjacent(d, d1);
                    }
                    case SinglePointInterval s -> {
                        yield areAdjacent(d, s);
                    }
                    case NoPointInterval ignored -> {
                        yield false;
                    }
                    default -> throw new UnsupportedOperationException("Unexpected value: " + b.getClass().getSimpleName());
                }
            }
            case SinglePointInterval s -> {
                switch (b) {
                    case DoublePointInterval d -> {
                        yield areAdjacent(d, s);
                    }
                    case SinglePointInterval s1 -> {
                        yield s.getPoint().equals(s1.getPoint());
                    }
                    case NoPointInterval ignored -> {
                        yield false;
                    }
                    default -> throw new UnsupportedOperationException("Unexpected value: " + b.getClass().getSimpleName());
                }
            }
            case NoPointInterval ignored -> false;
            default -> throw new UnsupportedOperationException("Not managed case"); // TODO (?)
        };

    }

    private static boolean areAdjacent(DoublePointInterval a, DoublePointInterval b) {

    /*
          a      b
       |-----|-----|
       al  ar=bl   br
       ar isClosed AND bl isClosed

          b      a
       |-----|-----|
       bl  br=al   ar
       br isClosed AND al isClosed

    */
        Delimiter al = a.getLeftDelimiter();
        Delimiter ar = a.getRightDelimiter();
        Delimiter bl = b.getLeftDelimiter();
        Delimiter br = b.getRightDelimiter();

        return (ar.getComponent().compareTo(bl.getComponent()) == 0 && ar.isClosed() && bl.isClosed()) ||
                (br.getComponent().compareTo(al.getComponent()) == 0 && br.isClosed() && al.isClosed());
    }

    private static boolean areAdjacent(DoublePointInterval a, SinglePointInterval b) {
        /*
            b   a
            |0-----|
           b=al     ar

              a   b
           |-----0|
           al   ar=b

        */

        int leftDistance = a.getLeftDelimiter().getComponent().compareTo(b.getPoint().getComponent());
        if (leftDistance == 0) {
            return a.getLeftDelimiter().isOpen();
        }

        int rightDistance = b.getPoint().getComponent().compareTo(a.getRightDelimiter().getComponent());
        if (rightDistance == 0) {
            return a.getRightDelimiter().isOpen();
        }

        return false;
    }

}
