package com.nemesis.mathcore.expressionsolver.models.intervals;

import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;

public class IntegerNumbersInterval implements GenericInterval {

    private String variable;

    public IntegerNumbersInterval(String variable) {
        this.variable = variable;
    }

    @Override
    public String getVariable() {
        return variable;
    }

    @Override
    public int compareTo(GenericInterval o) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public String toLatex() {
        return variable + " \\in \\Z";
    }

    public boolean contains(BigDecimal n) {
        return MathUtils.isIntegerValue(n);
    }
}
