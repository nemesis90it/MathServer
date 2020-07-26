package com.nemesis.mathcore.expressionsolver.utils;

import com.nemesis.mathcore.expressionsolver.exception.DisjointIntervalsException;
import com.nemesis.mathcore.expressionsolver.models.intervals.*;

/* Intervals summary:

    O-------------O     STRICTLY_BETWEEN          a<x<b
    |-------------|     BETWEEN                   a<=x<=b
    |-------------O     RIGHT_STRICTLY_BETWEEN    a<=x<b
    O-------------|     LEFT_STRICTLY_BETWEEN     a<x<=b
    |-------------      GREATER_THAN_OR_EQUALS    x>=a
    O-------------      GREATER_THAN              x>a
    --------------|     LESS_THAN_OR_EQUALS       x<=a
    --------------O     LESS_THAN                 x<a
    ---------------     -                         for each x
    OOOOOOO|OOOOOOO     -                         x=a
    -------O-------     -                         x!=a
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

        if (a instanceof DoublePointInterval aDpi && b instanceof DoublePointInterval bDpi) {
            if (areAdjacent(aDpi, bDpi)) {
                Delimiter al = aDpi.getLeftDelimiter();
                Delimiter ar = aDpi.getRightDelimiter();
                Delimiter bl = bDpi.getLeftDelimiter();
                Delimiter br = bDpi.getRightDelimiter();

                if (ar.getValue().compareTo(bl.getValue()) == 0 && ar.isClosed() && bl.isClosed()) {
                    return new SinglePointInterval(a.getVariable(), new Point(ar.getValue(), Point.Type.EQUALS));
                } else if (br.getValue().compareTo(al.getValue()) == 0 && br.isClosed() && al.isClosed()) {
                    return new SinglePointInterval(a.getVariable(), new Point(al.getValue(), Point.Type.EQUALS));
                } else {
                    throw new RuntimeException("Unexpected error: possible bug");
                }
            }

            final int leftDistance = bDpi.getLeftDelimiter().getValue().compareTo(aDpi.getLeftDelimiter().getValue());
            final Delimiter leftDelimiter = leftDistance >= 0 ? bDpi.getLeftDelimiter() : aDpi.getLeftDelimiter();

            final int rightDistance = aDpi.getRightDelimiter().getValue().compareTo(bDpi.getRightDelimiter().getValue());
            final Delimiter rightDelimiter = rightDistance > 0 ? aDpi.getRightDelimiter() : bDpi.getRightDelimiter();

            return new DoublePointInterval(a.getVariable(), leftDelimiter, rightDelimiter);
        } else {
            throw new UnsupportedOperationException("Not implemented yet"); // TODO
        }
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
            Delimiter al = aDpi.getLeftDelimiter();
            Delimiter ar = aDpi.getRightDelimiter();
            Delimiter bl = bDpi.getLeftDelimiter();
            Delimiter br = bDpi.getRightDelimiter();

            return bl.getValue().compareTo(ar.getValue()) > 0 ||
                    al.getValue().compareTo(br.getValue()) > 0 ||
                    (ar.getValue().compareTo(bl.getValue()) == 0 && ar.isOpen() && bl.isOpen()) ||
                    (br.getValue().compareTo(al.getValue()) == 0 && br.isOpen() && al.isOpen());
        } else {
            throw new UnsupportedOperationException("Not implemented yet"); // TODO
        }
    }

    public static boolean areAdjacent(DoublePointInterval a, DoublePointInterval b) {

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

}
