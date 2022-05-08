package com.nemesis.mathcore.expressionsolver.intervals.model;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.exception.VariablesMismatchException;
import com.nemesis.mathcore.expressionsolver.models.Stringable;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Union extends TreeSet<GenericInterval> implements GenericInterval {

    private final String variable;

    public Union(Set<GenericInterval> intervals) {
        super(intervals);
        Set<String> variables = intervals.stream().map(GenericInterval::getVariable).collect(Collectors.toSet());
        if (variables.size() > 1) {
            throw new VariablesMismatchException("Union between intervals must refer to the same variable");
        }
        this.variable = variables.stream().findFirst().orElse(null);
    }

    public Union(GenericInterval interval) {
        super();
        super.add(interval);
        this.variable = interval.getVariable();
    }

    public Union(GenericInterval... intervals) {
        super(List.of(intervals));
        Set<String> variables = Stream.of(intervals).map(GenericInterval::getVariable).collect(Collectors.toSet());
        if (variables.size() > 1) {
            throw new VariablesMismatchException("Union between intervals must refer to the same variable");
        }
        this.variable = variables.stream().findFirst().orElse(null);
    }

    @Override
    public String getVariable() {
        return variable;
    }

    @Override
    public boolean contains(Component c) {
        return this.stream().anyMatch(interval -> interval.contains(c));
    }

    @Override
    public GenericInterval getClone() {
        Set<GenericInterval> intervals = this.stream().map(GenericInterval::getClone).collect(Collectors.toSet());
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
}
