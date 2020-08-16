package com.nemesis.mathcore.expressionsolver.models.intervals;

import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;

public class NaturalNumbersInterval implements GenericInterval {

    private String variable;

    public NaturalNumbersInterval(String variable) {
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
        return variable + " \\in \\N";
    }

    public boolean contains(BigDecimal n) {
        return MathUtils.isIntegerValue(n) && n.compareTo(BigDecimal.ZERO) >= 0;
    }
}
