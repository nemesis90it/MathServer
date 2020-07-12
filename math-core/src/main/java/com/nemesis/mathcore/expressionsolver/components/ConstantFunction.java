package com.nemesis.mathcore.expressionsolver.components;

import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.math.BigDecimal;
import java.util.Objects;

public class ConstantFunction extends Constant {

    private Component component;

    public ConstantFunction(Component component) {
        if (!component.isScalar()) {
            throw new IllegalArgumentException("Cannot build constant: component [" + component + "] is not a scalar");
        }
        if (component instanceof ConstantFunction cf) {
            this.component = cf.getComponent();
        } else {
            this.component = component;
        }
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        if (!component.isScalar()) {
            throw new IllegalArgumentException("Cannot build constant: component [" + component + "] is not a scalar");
        }
        if (component instanceof ConstantFunction cf) {
            this.component = cf.getComponent();
        } else {
            this.component = component;
        }
    }

    @Override
    public BigDecimal getValue() {
        return component.getValue();
    }

    @Override
    public ConstantFunction getClone() {
        return new ConstantFunction(component.getClone());
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

    @Override
    public Component rewrite(Rule rule) {
        this.setComponent(this.getComponent().rewrite(rule));
        return this;
    }

    @Override
    public boolean contains(TermOperator termOperator) {
        return component.contains(termOperator);
    }

    @Override
    public boolean contains(Variable variable) {
        return component.contains(variable);
    }

    @Override
    public Domain getDomain(Variable variable) {
        return component.getDomain(variable);
    }
}
