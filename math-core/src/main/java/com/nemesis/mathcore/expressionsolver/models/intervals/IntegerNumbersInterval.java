package com.nemesis.mathcore.expressionsolver.models.intervals;

import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;

public class IntegerNumbersInterval extends DoublePointInterval {

    public IntegerNumbersInterval(String variable) {
        super(variable, Delimiter.MINUS_INFINITY, Delimiter.PLUS_INFINITY);
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
