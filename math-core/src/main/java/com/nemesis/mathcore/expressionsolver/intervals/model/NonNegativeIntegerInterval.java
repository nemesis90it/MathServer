package com.nemesis.mathcore.expressionsolver.intervals.model;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Infinity;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

public class NonNegativeIntegerInterval extends DoublePointInterval {

    public NonNegativeIntegerInterval(String variable, Delimiter leftDelimiter, Delimiter rightDelimiter) {

        super(variable, leftDelimiter, rightDelimiter);

        final Component leftValue = leftDelimiter.getComponent();
        final Component rightValue = rightDelimiter.getComponent();

        if (isInvalidDelimiter(leftValue) || isInvalidDelimiter(rightValue)) {
            throw new IllegalArgumentException("Delimiters must be positive integers or infinity");
        }
    }

    private boolean isInvalidDelimiter(Component leftValue) {
        return !(leftValue instanceof Infinity) && !ComponentUtils.isPositiveInteger(leftValue) && !ComponentUtils.isZero(leftValue);
    }

    @Override
    public boolean contains(Component c) {
        return ComponentUtils.isInteger(c) && super.contains(c);
    }

}
