package com.nemesis.mathcore.expressionsolver.intervals.utils;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Infinity;
import com.nemesis.mathcore.expressionsolver.exception.DisjointIntervalsException;
import com.nemesis.mathcore.expressionsolver.intervals.model.*;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Point;
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

        if (!a.getVariable().equals(b.getVariable())) {
            throw new IllegalArgumentException("Cannot intersect intervals on different variables");
        }

        if (a instanceof NoPointInterval || b instanceof NoPointInterval || IntervalsUtils.areDisjoint(a, b)) {
            return new NoPointInterval(a.getVariable());
        }

        String variable = a.getVariable();

        if (a instanceof N n) {
            if (b instanceof N) {
                return N.get(variable);
            } else if (b instanceof Z) {
                return N.get(variable);
            } else if (b instanceof PositiveIntegerInterval p) {
                return p;
            } else if (b instanceof DoublePointInterval d) {
                return intersect(variable, n, d);
            } else if (b instanceof SinglePointInterval s) {
                return intersect(variable, n, s);
            }
        }

        if (a instanceof Z z) {
            if (b instanceof N) {
                return N.get(variable);
            } else if (b instanceof Z) {
                return Z.get(variable);
            } else if (b instanceof PositiveIntegerInterval p) {
                return p;
            } else if (b instanceof DoublePointInterval d) {
                return intersect(variable, z, d);
            } else if (b instanceof SinglePointInterval s) {
                return intersect(variable, z, s);
            }
        }
        if (a instanceof PositiveIntegerInterval p) {
            if (b instanceof N) {
                return p;
            } else if (b instanceof Z) {
                return p;
            } else if (b instanceof PositiveIntegerInterval p2) {
                return intersect(variable, p, p2);
            } else if (b instanceof DoublePointInterval d) {
                return intersect(variable, p, d);
            } else if (b instanceof SinglePointInterval s) {
                return intersect(variable, p, s);
            }
        }

        if (a instanceof DoublePointInterval d) {
            if (b instanceof N n) {
                return intersect(variable, n, d);
            } else if (b instanceof Z z) {
                return intersect(variable, z, d);
            } else if (b instanceof PositiveIntegerInterval p) {
                return intersect(variable, p, d);
            } else if (b instanceof DoublePointInterval d2) {
                return intersect(variable, d, d2);
            } else if (b instanceof SinglePointInterval s) {
                return intersect(variable, d, s);
            }
        }

        if (a instanceof SinglePointInterval s) {
            if (b instanceof N n) {
                return intersect(variable, n, s);
            } else if (b instanceof Z z) {
                return intersect(variable, z, s);
            } else if (b instanceof PositiveIntegerInterval p) {
                return intersect(variable, p, s);
            } else if (b instanceof DoublePointInterval d) {
                return intersect(variable, d, s);
            } else if (b instanceof SinglePointInterval s2) {
                return intersect(variable, s, s2);
            }
        }

        throw new UnsupportedOperationException("Not supported"); // Should never happen (not managed cases)

    }

    private static GenericInterval intersect(String variable, N a, DoublePointInterval b) {
        throw new UnsupportedOperationException("Not supported"); // TODO
    }

    private static GenericInterval intersect(String variable, N a, SinglePointInterval b) {
        throw new UnsupportedOperationException("Not supported"); // TODO
    }

    private static GenericInterval intersect(String variable, Z a, DoublePointInterval b) {
        throw new UnsupportedOperationException("Not supported"); // TODO
    }

    private static GenericInterval intersect(String variable, Z a, SinglePointInterval b) {
        throw new UnsupportedOperationException("Not supported"); // TODO
    }

    private static GenericInterval intersect(String variable, PositiveIntegerInterval a, DoublePointInterval b) {
        throw new UnsupportedOperationException("Not supported"); // TODO
    }

    private static GenericInterval intersect(String variable, PositiveIntegerInterval a, SinglePointInterval b) {
        throw new UnsupportedOperationException("Not supported"); // TODO
    }

    private static GenericInterval intersect(String variable, SinglePointInterval a, SinglePointInterval b) {

        final Component aComponent = a.getPoint().getComponent();
        final Component bComponent = b.getPoint().getComponent();

        if (Objects.equals(aComponent.getValue(), bComponent.getValue()) && Objects.equals(a.getType(), b.getType())) {
            return new SinglePointInterval(variable, new Point(aComponent.getClone()), a.getType());
        } else {
            return new NoPointInterval(variable);
        }
    }

    private static GenericInterval intersect(String variable, DoublePointInterval a, DoublePointInterval b) {

        if (IntervalsUtils.areAdjacent(a, b)) {
            Delimiter al = a.getLeftDelimiter();
            Delimiter ar = a.getRightDelimiter();
            Delimiter bl = b.getLeftDelimiter();
            Delimiter br = b.getRightDelimiter();

            if (ar.getComponent().compareTo(bl.getComponent()) == 0 && ar.isClosed() && bl.isClosed()) {
                return new SinglePointInterval(variable, new Point(ar.getComponent()), SinglePointInterval.Type.EQUALS);
            } else if (br.getComponent().compareTo(al.getComponent()) == 0 && br.isClosed() && al.isClosed()) {
                return new SinglePointInterval(variable, new Point(al.getComponent()), SinglePointInterval.Type.EQUALS);
            } else {
                throw new RuntimeException("Unexpected error: possible bug");
            }
        }

        final int leftDistance = b.getLeftDelimiter().getComponent().compareTo(a.getLeftDelimiter().getComponent());
        final Delimiter leftDelimiter = leftDistance >= 0 ? b.getLeftDelimiter() : a.getLeftDelimiter();

        final int rightDistance = a.getRightDelimiter().getComponent().compareTo(b.getRightDelimiter().getComponent());
        final Delimiter rightDelimiter = rightDistance < 0 ? a.getRightDelimiter() : b.getRightDelimiter();

        if (a instanceof PositiveIntegerInterval || b instanceof PositiveIntegerInterval) {
            return new PositiveIntegerInterval(variable, leftDelimiter, rightDelimiter);
        } else {
            return new DoublePointInterval(variable, leftDelimiter, rightDelimiter);
        }

    }

    private static GenericInterval intersect(String variable, DoublePointInterval a, SinglePointInterval b) {

        if (IntervalsUtils.areAdjacent(a, b)) {
            return a;
        }

        throw new UnsupportedOperationException("Not implemented yet"); // TODO

    }

    public static GenericInterval merge(GenericInterval a, GenericInterval b) throws DisjointIntervalsException {

        if (!a.getVariable().equals(b.getVariable())) {
            throw new IllegalArgumentException("Cannot merge intervals on different variables");
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

        if (a instanceof Z && b instanceof Z) {
            return false;
        } else if (a instanceof NoPointInterval || b instanceof NoPointInterval) {
            return true;
        } else if (a instanceof Z aIni && b instanceof DoublePointInterval bDpi) {
            return areDisjoint(aIni, bDpi);
        } else if (a instanceof DoublePointInterval aDpi && b instanceof Z bIni) {
            return areDisjoint(bIni, aDpi);
        } else if (a instanceof DoublePointInterval aDpi && b instanceof DoublePointInterval bDpi) {
            return areDisjoint(aDpi, bDpi);
        } else if (a instanceof SinglePointInterval aSpi && b instanceof SinglePointInterval bSpi) {
            return !aSpi.getPoint().equals(bSpi.getPoint());
        } else if (a instanceof DoublePointInterval aDpi && b instanceof SinglePointInterval bSpi) {
            return areDisjoint(aDpi, bSpi);
        } else if (a instanceof SinglePointInterval aSpi && b instanceof DoublePointInterval bDpi) {
            return areDisjoint(bDpi, aSpi);
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

    public static boolean areAdjacent(GenericInterval a, GenericInterval b) {

        if (a instanceof N aN && b instanceof N bN) {
            throw new UnsupportedOperationException("Not implemented yet"); // TODO
        } else if (a instanceof N aN && b instanceof DoublePointInterval bDpi) {
            throw new UnsupportedOperationException("Not implemented yet"); // TODO
        } else if (a instanceof DoublePointInterval aDpi && b instanceof N bN) {
            throw new UnsupportedOperationException("Not implemented yet"); // TODO
        } else if (a instanceof DoublePointInterval && b instanceof DoublePointInterval) {
            return areAdjacent(a, b);
        } else if (a instanceof DoublePointInterval && b instanceof SinglePointInterval bSpi) {
            return areAdjacent(a, bSpi);
        } else if (a instanceof SinglePointInterval aSpi && b instanceof DoublePointInterval) {
            return areAdjacent(b, aSpi);
        } else if (a instanceof SinglePointInterval aSpi && b instanceof SinglePointInterval bSpi) {
            return aSpi.getPoint().equals(bSpi.getPoint());
        } else {
            throw new UnsupportedOperationException("Not implemented yet"); // TODO (?)  (not managed cases)
        }
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
