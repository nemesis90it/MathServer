package com.nemesis.mathcore.expressionsolver.intervals.model;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.exception.VariablesMismatchException;

import java.util.Objects;

public class GenericIntersection implements GenericInterval {

    private final GenericInterval a;
    private final GenericInterval b;


    public GenericIntersection(GenericInterval a, GenericInterval b) {
        this.a = a;
        this.b = b;
        if (!Objects.equals(a.getVariable(), a.getVariable())) {
            throw new VariablesMismatchException(a.getVariable(), a.getVariable());
        }

    }

    @Override
    public String getVariable() {
        return a.getVariable();
    }

    @Override
    public boolean contains(Component c) {
        return a.contains(c) && b.contains(c);
    }

    @Override
    public int compareTo(GenericInterval o) {
        return 0;
    }

    @Override
    public String toLatex() {
        return a.toLatex() + " \\cap " + b.toLatex();
    }

    @Override
    public String toString() {
        return a.toString() + " ∩ " + b.toString();
    }
}
