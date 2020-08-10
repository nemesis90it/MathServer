package com.nemesis.mathcore.expressionsolver.utils;

import com.nemesis.mathcore.expressionsolver.exception.DisjointIntervalsException;
import com.nemesis.mathcore.expressionsolver.models.intervals.*;

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

        if (areDisjoint(a, b)) {
            return new NoPointInterval(a.getVariable());
        }

        String variable = a.getVariable();

        if (a instanceof NoPointInterval || b instanceof NoPointInterval) {
            return new NoPointInterval(a.getVariable());
        } else if (a instanceof DoublePointInterval aDpi && b instanceof DoublePointInterval bDpi) {
            return intersect(variable, aDpi, bDpi);
        } else if (a instanceof DoublePointInterval aDpi && b instanceof SinglePointInterval bSpi) {
            return intersect(variable, aDpi, bSpi);
        } else if (a instanceof SinglePointInterval aSpi && b instanceof DoublePointInterval bDpi) {
            return intersect(variable, bDpi, aSpi);
        } else {
            throw new UnsupportedOperationException("Not implemented yet"); // Should never happen
        }
    }

    private static GenericInterval intersect(String variable, DoublePointInterval a, DoublePointInterval b) {

        if (areAdjacent(a, b)) {
            Delimiter al = a.getLeftDelimiter();
            Delimiter ar = a.getRightDelimiter();
            Delimiter bl = b.getLeftDelimiter();
            Delimiter br = b.getRightDelimiter();

            if (ar.getValue().compareTo(bl.getValue()) == 0 && ar.isClosed() && bl.isClosed()) {
                return new SinglePointInterval(variable, new Point(ar.getValue(), Point.Type.EQUALS));
            } else if (br.getValue().compareTo(al.getValue()) == 0 && br.isClosed() && al.isClosed()) {
                return new SinglePointInterval(variable, new Point(al.getValue(), Point.Type.EQUALS));
            } else {
                throw new RuntimeException("Unexpected error: possible bug");
            }
        }

        final int leftDistance = b.getLeftDelimiter().getValue().compareTo(a.getLeftDelimiter().getValue());
        final Delimiter leftDelimiter = leftDistance >= 0 ? b.getLeftDelimiter() : a.getLeftDelimiter();

        final int rightDistance = a.getRightDelimiter().getValue().compareTo(b.getRightDelimiter().getValue());
        final Delimiter rightDelimiter = rightDistance > 0 ? a.getRightDelimiter() : b.getRightDelimiter();

        return new DoublePointInterval(variable, leftDelimiter, rightDelimiter);
    }

    private static GenericInterval intersect(String variable, DoublePointInterval a, SinglePointInterval b) {

        if (areAdjacent(a, b)) {
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
            final int leftDistance = bDpi.getLeftDelimiter().getValue().compareTo(aDpi.getLeftDelimiter().getValue());
            final Delimiter leftDelimiter = leftDistance > 0 ? aDpi.getLeftDelimiter() : bDpi.getLeftDelimiter();

            final int rightDistance = aDpi.getRightDelimiter().getValue().compareTo(bDpi.getRightDelimiter().getValue());
            final Delimiter rightDelimiter = rightDistance > 0 ? bDpi.getRightDelimiter() : aDpi.getRightDelimiter();

            // TODO: left distance or right distance = 0 ? Check delimiters type (open/closed)

            return new DoublePointInterval(a.getVariable(), leftDelimiter, rightDelimiter);
        } else {
            throw new UnsupportedOperationException("Not implemented yet"); // TODO
        }

    }

    public static boolean areDisjoint(GenericInterval a, GenericInterval b) {

        if (a instanceof DoublePointInterval aDpi && b instanceof DoublePointInterval bDpi) {
            return areDisjoint(aDpi, bDpi);
        } else if (a instanceof SinglePointInterval aSpi && b instanceof SinglePointInterval bSpi) {
            return !aSpi.getPoint().equals(bSpi.getPoint());
        } else if (a instanceof DoublePointInterval aDpi && b instanceof SinglePointInterval bSpi) {
            return areDisjoint(aDpi, bSpi);
        } else if (a instanceof SinglePointInterval aSpi && b instanceof DoublePointInterval bDpi) {
            return areDisjoint(bDpi, aSpi);
        } else {
            throw new UnsupportedOperationException("Not implemented yet"); // Should never happen
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

        return bl.getValue().compareTo(ar.getValue()) > 0 ||
                al.getValue().compareTo(br.getValue()) > 0 ||
                (ar.getValue().compareTo(bl.getValue()) == 0 && ar.isOpen() && bl.isOpen()) ||
                (br.getValue().compareTo(al.getValue()) == 0 && br.isOpen() && al.isOpen());
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

        int leftDistance = a.getLeftDelimiter().getValue().compareTo(b.getPoint().getValue());
        if (leftDistance >= 0) {
            return leftDistance > 0 || a.getLeftDelimiter().isOpen();
        }

        int rightDistance = b.getPoint().getValue().compareTo(a.getRightDelimiter().getValue());
        if (rightDistance >= 0) {
            return rightDistance > 0 || a.getRightDelimiter().isOpen();
        }

        return false;
    }

    public static boolean areAdjacent(GenericInterval a, GenericInterval b) {

        if (a instanceof DoublePointInterval && b instanceof DoublePointInterval) {
            return areAdjacent(a, b);
        } else if (a instanceof DoublePointInterval && b instanceof SinglePointInterval bSpi) {
            return areAdjacent(a, bSpi);
        } else if (a instanceof SinglePointInterval aSpi && b instanceof DoublePointInterval) {
            return areAdjacent(b, aSpi);
        } else if (a instanceof SinglePointInterval aSpi && b instanceof SinglePointInterval bSpi) {
            return aSpi.getPoint().equals(bSpi.getPoint());
        } else {
            throw new UnsupportedOperationException("Not implemented yet"); // TODO
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

        return (ar.getValue().compareTo(bl.getValue()) == 0 && ar.isClosed() && bl.isClosed()) ||
                (br.getValue().compareTo(al.getValue()) == 0 && br.isClosed() && al.isClosed());
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

        int leftDistance = a.getLeftDelimiter().getValue().compareTo(b.getPoint().getValue());
        if (leftDistance == 0) {
            return a.getLeftDelimiter().isOpen();
        }

        int rightDistance = b.getPoint().getValue().compareTo(a.getRightDelimiter().getValue());
        if (rightDistance == 0) {
            return a.getRightDelimiter().isOpen();
        }

        return false;
    }

}
