package com.nemesis.mathcore.expressionsolver.intervals.model;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.util.HashMap;
import java.util.Map;

public class Z extends DoublePointInterval {

    private static final Map<String, Z> cache = new HashMap<>();

    public static Z of(String variable) {
        return cache.computeIfAbsent(variable, Z::new);
    }

    protected Z(String variable) {
        super(variable, Delimiter.MINUS_INFINITY, Delimiter.PLUS_INFINITY);
    }

    @Override
    public String getVariable() {
        return variable;
    }

    @Override
    public int compareTo(GenericInterval o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NumericDomain getDomain() {
        return NumericDomain.Z;
    }

    @Override
    public String toLatex() {
        return variable + " \\in \\mathbb{Z}";
    }

    @Override
    public String toString() {
        return super.variable + " ∈ ℤ";
    }

    public boolean contains(Component c) {
        return ComponentUtils.isInteger(c);
    }

    @Override
    public GenericInterval getClone() {
        return Z.of(variable);
    }
}
