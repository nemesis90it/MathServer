package com.nemesis.mathcore.expressionsolver.models;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Intervals extends TreeSet<GenericInterval> {
    public Intervals(Set<GenericInterval> intervals) {
        super(intervals);
    }

    public Intervals(GenericInterval interval) {
        super();
        super.add(interval);
    }

    public String toPlainString() {
        List<String> plainStrings = super.stream().map(GenericInterval::toString).collect(Collectors.toList());
        return String.join(" , ", plainStrings);
    }

    public String toLatexString() {
        List<String> latexStrings = super.stream().map(GenericInterval::toLatex).collect(Collectors.toList());
        return String.join(" , ", latexStrings);
    }
}
