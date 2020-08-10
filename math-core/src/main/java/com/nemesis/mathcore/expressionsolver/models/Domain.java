package com.nemesis.mathcore.expressionsolver.models;


import com.nemesis.mathcore.expressionsolver.models.intervals.GenericInterval;
import com.nemesis.mathcore.expressionsolver.models.intervals.Intervals;
import com.nemesis.mathcore.expressionsolver.utils.IntervalsUtils;
import lombok.Data;

import java.util.Iterator;
import java.util.Set;

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

    public void addInterval(GenericInterval interval) {

        if (intervals.isEmpty()) {
            intervals.add(interval);
            return;
        }

        for (Iterator<GenericInterval> iterator = this.intervals.iterator(); iterator.hasNext(); ) {
            GenericInterval thisInterval = iterator.next();
            final GenericInterval newInterval;
            if (IntervalsUtils.areDisjoint(thisInterval, interval)) {
                newInterval = interval;
            } else {
                iterator.remove();
                newInterval = IntervalsUtils.intersect(thisInterval, interval);
            }
            intervals.add(newInterval);
        }
    }

    public void addIntervals(Set<GenericInterval> intervals) {
        intervals.forEach(this::addInterval);
    }

    public String toLatex() {
        return intervals.toLatexString();
    }
}
