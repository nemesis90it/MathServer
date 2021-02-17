package com.nemesis.mathcore.expressionsolver.models;


import com.nemesis.mathcore.expressionsolver.exception.DisjointIntervalsException;
import com.nemesis.mathcore.expressionsolver.intervals.model.GenericInterval;
import com.nemesis.mathcore.expressionsolver.intervals.model.Intervals;
import com.nemesis.mathcore.expressionsolver.intervals.utils.IntervalsUtils;
import lombok.Data;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

// TODO: check variables (must be the same for all intervals)

@Data
public class Domain {

    private final Intervals intervals = new Intervals();

    public Domain() {
    }

    public Domain(GenericInterval interval) {
        this.intervals.add(interval);
    }

    public Domain(Intervals intervals) {
        this.intervals.addAll(intervals);
    }

    public void unionWith(GenericInterval interval) {
        if (intervals.isEmpty()) {
            intervals.add(interval);
            return;
        }

        Set<GenericInterval> intervalsToAdd = new TreeSet<>();

        for (Iterator<GenericInterval> iterator = this.intervals.iterator(); iterator.hasNext(); ) {
            GenericInterval thisInterval = iterator.next();
            if (IntervalsUtils.areDisjoint(thisInterval, interval)) {
                intervalsToAdd.add(interval);
            } else {
                try {
                    GenericInterval newInterval = IntervalsUtils.merge(thisInterval, interval);
                    intervalsToAdd.add(newInterval);
                } catch (DisjointIntervalsException e) {
                    // should never happen
                    throw new RuntimeException(e);
                }
                iterator.remove();
            }
        }
        intervals.addAll(intervalsToAdd);
    }

    public void intersectWith(GenericInterval interval) {

        if (intervals.isEmpty()) {
            intervals.add(interval);
            return;
        }

        Set<GenericInterval> intervalsToAdd = new TreeSet<>();

        for (Iterator<GenericInterval> iterator = this.intervals.iterator(); iterator.hasNext(); ) {
            GenericInterval thisInterval = iterator.next();
            final GenericInterval newInterval;
            if (!IntervalsUtils.areDisjoint(thisInterval, interval)) {
                newInterval = IntervalsUtils.intersect(thisInterval, interval);
                intervalsToAdd.add(newInterval);
            }
            iterator.remove();
        }
        intervals.addAll(intervalsToAdd);
    }

    public void intersectWith(Set<GenericInterval> intervals) {
        intervals.forEach(this::intersectWith);
    }

    public String toLatex() {
        return intervals.toLatexString();
    }

    public String toString() {
        return intervals.toPlainString();
    }
}






