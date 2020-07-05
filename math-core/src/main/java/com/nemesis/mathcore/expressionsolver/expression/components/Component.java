package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.exception.NoValueException;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;
import java.util.Set;

public abstract class Component implements Comparable<Component> {

    BigDecimal value = null;

    public abstract BigDecimal getValue();

    public abstract Component getDerivative(Variable var);

    public abstract Component rewrite(Rule rule);

    public abstract Boolean isScalar();

    public Constant getValueAsConstant() {

        if (!this.isScalar()) {
            throw new NoValueException("This component is not a scalar");
        }

        BigDecimal value = this.getValue();
        if (!MathUtils.isIntegerValue(value) && MathCoreContext.getNumericMode() == MathCoreContext.Mode.FRACTIONAL) {
            return new ConstantFunction(this.getClone());
        } else {
            return new Constant(value);
        }
    }

    public boolean contains(TermOperator termOperator) {
        return false;
    }

    public abstract boolean contains(Variable variable);

    public String toLatex() {
        return this.toString();
    }

    public abstract Component getClone();

    public abstract Domain getDomain(Variable variable);

    public abstract Set<Variable> getVariables();
}
