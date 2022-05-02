package com.nemesis.mathcore.expressionsolver.intervals.model;

import com.nemesis.mathcore.expressionsolver.components.Component;

public class NoPointInterval implements GenericInterval {

    private final String variable;

    public NoPointInterval(String variable) {
        this.variable = variable;
    }

    @Override
    public String toString() {
        return String.format("%s ∈ ∅", variable);
    }

    @Override
    public String toLatex() {
        return String.format("%s \\in \\emptyset", variable);
    }

    @Override
    public int compareTo(GenericInterval o) {
        return -1;
    }

    @Override
    public String getVariable() {
        return variable;
    }

    @Override
    public boolean contains(Component c) {
        return false;
    }

    @Override
    public GenericInterval getClone() {
        return new NoPointInterval(variable);
    }
}
