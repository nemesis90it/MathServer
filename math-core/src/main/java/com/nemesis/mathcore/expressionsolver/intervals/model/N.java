package com.nemesis.mathcore.expressionsolver.intervals.model;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Constant;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.util.HashMap;
import java.util.Map;

public class N extends DoublePointInterval {

    private static final Map<String, N> cache = new HashMap<>();

    public static N of(String variable) {
        return cache.computeIfAbsent(variable, N::new);
    }

    private N(String variable) {
        super(variable, new Delimiter(Delimiter.Type.CLOSED, Constant.ZERO), Delimiter.PLUS_INFINITY);
    }

    @Override
    public int compareTo(GenericInterval o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NumericDomain getDomain() {
        return NumericDomain.N;
    }

    @Override
    public String toLatex() {
        return super.variable + " \\in \\N";
    }

    @Override
    public String toString() {
        return super.variable + " ∈ ℕ";
    }

    @Override
    public boolean contains(Component c) {
        return ComponentUtils.isInteger(c) && (ComponentUtils.isZero(c) || ComponentUtils.isPositive(c));
    }

    @Override
    public GenericInterval getClone() {
        return N.of(variable);
    }
}
