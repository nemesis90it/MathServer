package com.nemesis.mathcore.expressionsolver.intervals.model;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.exception.VariablesMismatchException;
import com.nemesis.mathcore.expressionsolver.models.Stringable;

import java.util.*;
import java.util.stream.Collectors;

public class Union extends TreeSet<GenericInterval> implements GenericInterval {

    private String variable;

    public Union(Set<GenericInterval> intervals) {
        super();
        this.addIntervals(intervals);
        this.setVariable(this);
    }

    public Union() {
    }

    public Union(GenericInterval interval) {
        this.addInterval(interval);
    }

    public Union(GenericInterval... intervals) {
        this(Set.of(intervals));
    }

    public void addInterval(GenericInterval interval) {
        this.variable = interval.getVariable();
        if (interval instanceof NoPointInterval && !super.isEmpty()) {
            return; // the union with void interval has no effect then it will not be added, but if there are no intervals yet, union is a single NoPointInterval
        }
        super.add(interval);
    }

    @Override
    public boolean addAll(Collection<? extends GenericInterval> intervals) {
        this.setVariable(this);
        this.addIntervals(intervals);
        return true;
    }

    private void addIntervals(Collection<? extends GenericInterval> intervals) {

        if (intervals.isEmpty()) {
            return;
        }

        if (intervals.stream().allMatch(i -> i instanceof NoPointInterval)) {
            // if all intervals are void, union is a single NoPointInterval
            super.clear();
            GenericInterval firstVoidInterval = intervals.stream().findFirst().orElse(null);
            super.add(firstVoidInterval);
        } else {
            // else remove the void ones, because the union with others (non-void) intervals has no effect
            super.addAll(intervals);
            super.removeIf(i -> i instanceof NoPointInterval);
        }
    }

    @Override
    public String getVariable() {
        return variable;
    }

    @Override
    public boolean contains(Component c) {
        return super.stream().anyMatch(interval -> interval.contains(c));
    }

    @Override
    public GenericInterval getClone() {
        Set<GenericInterval> intervals = super.stream().map(GenericInterval::getClone).collect(Collectors.toSet());
        return new Union(intervals);
    }

    @Override
    public int compareTo(GenericInterval o) {
        return 0;
    }

    public String toString() {
        List<String> plainStrings = super.stream()
                .map(i -> i instanceof Intersection ? "(" + i + ")" : i.toString())
                .collect(Collectors.toCollection(LinkedList::new));
        return String.join(" ∪ ", plainStrings);
    }

    public String toLatex() {
        List<String> latexStrings = super.stream().map(Stringable::toLatex).collect(Collectors.toCollection(LinkedList::new));
        return String.join(" \\cup ", latexStrings);
    }

    private void setVariable(Set<GenericInterval> intervals) {
        Set<String> variables = intervals.stream().map(GenericInterval::getVariable).collect(Collectors.toSet());
        if (variables.size() > 1) {
            throw new VariablesMismatchException("Union between intervals must refer to the same variable");
        }
        this.variable = variables.stream().findFirst().orElse(null);
    }

}
