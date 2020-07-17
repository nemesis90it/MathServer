package com.nemesis.mathcore.expressionsolver.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Domain {

    private Intervals intervals = new Intervals();

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

    public String toLatex() {
        return intervals.toLatexString();
    }
}
