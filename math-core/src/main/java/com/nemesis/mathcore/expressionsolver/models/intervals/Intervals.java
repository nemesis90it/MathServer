package com.nemesis.mathcore.expressionsolver.models.intervals;

import com.nemesis.mathcore.expressionsolver.components.Stringable;

import java.util.LinkedList;
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

    public Intervals() {
        super();
    }

    public String toPlainString() {
        List<String> plainStrings = super.stream().map(Stringable::toString).collect(Collectors.toCollection(LinkedList::new));
        return String.join(" , ", plainStrings);
    }

    public String toLatexString() {
        List<String> latexStrings = super.stream().map(Stringable::toLatex).collect(Collectors.toCollection(LinkedList::new));
        return String.join(" , ", latexStrings);
    }
}
