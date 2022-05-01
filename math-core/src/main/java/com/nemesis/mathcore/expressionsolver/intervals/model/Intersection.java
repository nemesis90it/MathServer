package com.nemesis.mathcore.expressionsolver.intervals.model;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.exception.VariablesMismatchException;

import java.util.Objects;

public class Intersection implements GenericInterval {

    private final GenericInterval a;
    private final GenericInterval b;

    public Intersection(GenericInterval a, GenericInterval b) {
        if (!Objects.equals(a.getVariable(), a.getVariable())) {
            throw new VariablesMismatchException("intersection between two intervals must refer to the same variable");
        }
        this.a = a;
        this.b = b;
    }

    @Override
    public String getVariable() {
        return a.getVariable();
    }

    public GenericInterval getA() {
        return a.getClone();
    }

    public GenericInterval getB() {
        return b.getClone();
    }

    @Override
    public boolean contains(Component c) {
        return a.contains(c) && b.contains(c);
    }

    @Override
    public GenericInterval getClone() {
        return new Intersection(a.getClone(), b.getClone());
    }

    @Override
    public int compareTo(GenericInterval o) {
        return 0;
    }

    @Override
    public String toLatex() {
        String aLatex = a instanceof Union ? "(" + a.toLatex() + ")" : a.toLatex();
        String bLatex = b instanceof Union ? "(" + b.toLatex() + ")" : b.toLatex();
        return aLatex + " \\cap " + bLatex;
    }

    @Override
    public String toString() {
        String aString = a instanceof Union ? "(" + a + ")" : a.toString();
        String bString = b instanceof Union ? "(" + b + ")" : b.toString();
        return aString + " ∩ " + bString;
    }
}
