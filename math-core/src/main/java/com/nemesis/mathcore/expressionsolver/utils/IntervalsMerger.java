package com.nemesis.mathcore.expressionsolver.utils;

import com.nemesis.mathcore.expressionsolver.exception.DisjointIntervalsException;
import com.nemesis.mathcore.expressionsolver.models.intervals.Delimiter;
import com.nemesis.mathcore.expressionsolver.models.intervals.GenericInterval;
import com.nemesis.mathcore.expressionsolver.models.intervals.DoublePointInterval;

/*
    O-------------O     STRICTLY_BETWEEN          a<x<b
    |-------------|     BETWEEN                   a<=x<=b
    |-------------O     RIGHT_STRICTLY_BETWEEN    a<=x<b
    O-------------|     LEFT_STRICTLY_BETWEEN     a<x<=b
    |-------------      GREATER_THAN_OR_EQUALS    x>=a
    O-------------      GREATER_THAN              x>a
    --------------|     LESS_THAN_OR_EQUALS       x<=a
    --------------O     LESS_THAN                 x<a
    OOOOOOO|OOOOOOO     ()    x=a
    -------O-------     ()    x!=a
    OOOOOOOOOOOOOOO     ()    no x
    ---------------     ()    for each x

 */

public class IntervalsMerger {


    private GenericInterval merge(DoublePointInterval a, DoublePointInterval b) throws DisjointIntervalsException {

        if (areDisjoint(a, b)) {
            throw new DisjointIntervalsException("Cannot merge disjoint intervals");
        }

        final Delimiter leftDelimiter = a.getLeftDelimiter().getValue().compareTo(b.getLeftDelimiter().getValue()) < 0 ?
                a.getLeftDelimiter() : b.getLeftDelimiter();

        final Delimiter rightDelimiter = a.getRightDelimiter().getValue().compareTo(b.getRightDelimiter().getValue()) > 0 ?
                a.getRightDelimiter() : b.getRightDelimiter();


        return new DoublePointInterval(a.getVariable(), leftDelimiter, rightDelimiter);


    }

    private boolean areDisjoint(DoublePointInterval a, DoublePointInterval b) {

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

}
