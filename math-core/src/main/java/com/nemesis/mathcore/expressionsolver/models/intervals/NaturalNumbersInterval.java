package com.nemesis.mathcore.expressionsolver.models.intervals;

import com.nemesis.mathcore.expressionsolver.components.Constant;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;

public class NaturalNumbersInterval extends PositiveIntegerInterval {

    public NaturalNumbersInterval(String variable) {
        super(variable, new Delimiter(Delimiter.Type.CLOSED, new Constant(0)), Delimiter.PLUS_INFINITY);
    }

    @Override
    public int compareTo(GenericInterval o) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public String toLatex() {
        return super.variable + " \\in \\N";
    }

    @Override
    public boolean contains(BigDecimal n) {
        return MathUtils.isIntegerValue(n) && n.compareTo(BigDecimal.ZERO) >= 0;
    }
}
