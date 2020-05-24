package com.nemesis.mathcore.expressionsolver.expression.components;

import java.math.BigDecimal;
import java.util.Objects;

public class ConstantFunction extends Constant {

    private Component component;

    public ConstantFunction(Component component) {
        if (!component.isScalar()) {
            throw new IllegalArgumentException("Cannot build constant: component [" + component + "] is not a scalar");
        }
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    @Override
    public BigDecimal getValue() {
        return component.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ConstantFunction that = (ConstantFunction) o;
        return Objects.equals(component, that.component);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), component);
    }

    @Override
    public String toString() {
        return component.toString();
    }

    @Override
    public String toLatex() {
        return component.toLatex();
    }
}
