package com.nemesis.mathcore.expressionsolver.models.intervals;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Constant;
import com.nemesis.mathcore.expressionsolver.components.Infinity;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;

public class PositiveIntegerInterval extends DoublePointInterval {

    public PositiveIntegerInterval(String variable, Delimiter leftDelimiter, Delimiter rightDelimiter, Type type) {

        super(variable, leftDelimiter, rightDelimiter, type);

        final Component leftValue = leftDelimiter.getComponent();
        final Component rightValue = rightDelimiter.getComponent();

        if ((!(leftValue instanceof Infinity) && !isPositiveInteger(leftValue)) ||
                (!(rightValue instanceof Infinity) && !isPositiveInteger(rightValue))) {
            throw new IllegalArgumentException("Delimiters must be positive integers or infinity");
        }

    }

    private boolean isPositiveInteger(Component component) {
        return component.isScalar() && isPositiveInteger(component.getValue());
    }

    private boolean isPositiveInteger(BigDecimal value) {
        return MathUtils.isIntegerValue(value) && value.compareTo(BigDecimal.ZERO) >= 0;
    }

    public PositiveIntegerInterval(String variable, Delimiter leftDelimiter, Delimiter rightDelimiter) {
        super(variable, leftDelimiter, rightDelimiter);
    }

    public PositiveIntegerInterval(String variable, Type type, Component leftValue, Component rightValue) {
        super(variable, type, leftValue, rightValue);
    }

    @Override
    public int compareTo(GenericInterval o) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public String toLatex() {
        throw new UnsupportedOperationException(); // TODO
    }

    public boolean contains(BigDecimal n) {
        return isPositiveInteger(n) && checkLeftDelimiter(n) && checkRightDelimiter(n);
    }

    private boolean checkLeftDelimiter(BigDecimal n) {
        Component c = new Constant(n);
        return getLeftDelimiter().getType().equals(Delimiter.Type.OPEN) ?
                c.compareTo(getLeftDelimiter().getComponent()) > 0 :
                c.compareTo(getLeftDelimiter().getComponent()) >= 0;
    }

    private boolean checkRightDelimiter(BigDecimal n) {
        Component c = new Constant(n);
        return getRightDelimiter().getType().equals(Delimiter.Type.OPEN) ?
                c.compareTo(getRightDelimiter().getComponent()) < 0 :
                c.compareTo(getRightDelimiter().getComponent()) <= 0;
    }

}
