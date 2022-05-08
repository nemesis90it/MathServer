package com.nemesis.mathcore.expressionsolver.intervals.model;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Infinity;
import com.nemesis.mathcore.expressionsolver.intervals.utils.DoublePointIntervalStringifier;
import com.nemesis.mathcore.expressionsolver.models.Stringable;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

public class SubSetZ extends DoublePointInterval {

    public SubSetZ(String variable, Delimiter leftDelimiter, Delimiter rightDelimiter) {

        super(variable, leftDelimiter, rightDelimiter);

        final Component leftValue = leftDelimiter.getComponent();
        final Component rightValue = rightDelimiter.getComponent();

        if (isInvalidDelimiter(leftValue) || isInvalidDelimiter(rightValue)) {
            throw new IllegalArgumentException("Delimiters must be integers or infinity");
        }
    }

    private boolean isInvalidDelimiter(Component c) {
        return !(c instanceof Infinity) && !ComponentUtils.isInteger(c);
    }

    @Override
    public NumericDomain getDomain() {
        return NumericDomain.Z;
    }

    @Override
    public boolean contains(Component c) {
        return ComponentUtils.isInteger(c) && super.contains(c);
    }

    @Override
    public GenericInterval getClone() {
        return new SubSetZ(variable, super.getLeftDelimiter(), super.getRightDelimiter());
    }

    @Override
    public String toString() {
        return DoublePointIntervalStringifier.stringhify(this, Stringable::toString);
    }

    @Override
    public String toLatex() {
        return DoublePointIntervalStringifier.stringhify(this, Stringable::toLatex);
    }
}
