package com.nemesis.mathcore.expressionsolver.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.TreeSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Domain {

    private Set<GenericInterval> intervals = new TreeSet<>();

    public Domain(GenericInterval interval) {
        this.intervals.add(interval);
    }

    public void addInterval(GenericInterval interval) {
        //TODO: merge intervals, if possible
        this.intervals.add(interval);
    }

    public void addIntervals(Set<GenericInterval> intervals) {
        //TODO: merge intervals, if possible
        this.intervals.addAll(intervals);
    }
}
