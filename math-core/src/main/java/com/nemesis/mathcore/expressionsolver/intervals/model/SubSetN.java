package com.nemesis.mathcore.expressionsolver.intervals.model;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Infinity;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import org.apache.commons.lang3.StringUtils;

public class SubSetN extends SubSetZ {

    public SubSetN(String variable, Delimiter leftDelimiter, Delimiter rightDelimiter) {

        super(variable, leftDelimiter, rightDelimiter);

        final Component leftValue = leftDelimiter.getComponent();
        final Component rightValue = rightDelimiter.getComponent();

        if (isInvalidDelimiter(leftValue) || isInvalidDelimiter(rightValue)) {
            throw new IllegalArgumentException("Delimiters must be positive integers or plus infinity");
        }
    }

    private boolean isInvalidDelimiter(Component c) {
        return !(c instanceof Infinity) && !ComponentUtils.isPositiveInteger(c);
    }

    @Override
    public boolean contains(Component c) {
        return ComponentUtils.isPositiveInteger(c) && super.contains(c);
    }

    @Override
    public String toLatex() {
        return StringUtils.substringBefore(super.toLatex(), ",") + ", " + super.variable + " \\in \\N";
    }

    @Override
    public String toString() {
        return StringUtils.substringBefore(super.toString(), ",") + ", " + super.variable + " ∈ ℕ";
    }

}
