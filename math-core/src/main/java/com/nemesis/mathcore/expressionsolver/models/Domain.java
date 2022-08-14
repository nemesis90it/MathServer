package com.nemesis.mathcore.expressionsolver.models;


import com.nemesis.mathcore.expressionsolver.exception.DisjointIntervalsException;
import com.nemesis.mathcore.expressionsolver.intervals.model.GenericInterval;
import com.nemesis.mathcore.expressionsolver.intervals.model.NoPointInterval;
import com.nemesis.mathcore.expressionsolver.intervals.model.Union;
import com.nemesis.mathcore.expressionsolver.intervals.utils.IntervalsUtils;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

// TODO: check variables (must be the same for all intervals)

/*
    Domain is the union of one or more intervals. Note that some intervals can be intersections.
    Example: x<0 ∪ (x>3 ∩ x<5)
*/

public class Domain {

    private final Union unitedIntervals = new Union();

    public Domain() {
    }

    public Domain(GenericInterval interval) {
        this.unitedIntervals.addInterval(interval);
    }

    public Domain(Union intervals) {
        this.unitedIntervals.addInterval(intervals);
    }

    public Set<GenericInterval> getIntervals() {
        return unitedIntervals;
    }

    public void unionWith(GenericInterval interval) {
        if (unitedIntervals.isEmpty()) {
            unitedIntervals.addInterval(interval);
            return;
        }

        Set<GenericInterval> intervalsToAdd = new TreeSet<>();

        for (Iterator<GenericInterval> iterator = this.unitedIntervals.iterator(); iterator.hasNext(); ) {
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
        unitedIntervals.addAll(intervalsToAdd);
    }

    public void intersectWith(GenericInterval interval) {

        if (unitedIntervals.isEmpty()) {
            unitedIntervals.addInterval(interval);
            return;
        }

        Set<GenericInterval> intervalsToAdd = new TreeSet<>();

        for (Iterator<GenericInterval> iterator = this.unitedIntervals.iterator(); iterator.hasNext(); ) {
            GenericInterval thisInterval = iterator.next();
            final GenericInterval newInterval;
            if (!IntervalsUtils.areDisjoint(thisInterval, interval)) {
                newInterval = IntervalsUtils.intersect(thisInterval, interval);
                intervalsToAdd.add(newInterval);
            }
            iterator.remove();
        }
        if (unitedIntervals.isEmpty() && interval instanceof NoPointInterval) {
            unitedIntervals.add(interval);
        } else {
            unitedIntervals.addAll(intervalsToAdd);
        }
    }

    public void intersectWith(Set<GenericInterval> intervals) {
        intervals.forEach(this::intersectWith);
    }

    public String toLatex() {
        return unitedIntervals.toLatex();
    }

    public String toString() {
        return unitedIntervals.toString();
    }
}






