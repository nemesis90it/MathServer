package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.math.BigDecimal;

public abstract class Component implements Comparable<Component> {

    BigDecimal value = null;

    public abstract BigDecimal getValue();

    public abstract Component getDerivative(char var);

    public abstract Component rewrite(Rule rule);

    public abstract Boolean isScalar();

    public abstract Constant getValueAsConstant();

    public boolean contains(TermOperator termOperator) {
        return false;
    }

    public abstract boolean contains(Variable variable);

    public String toLatex() {
        return this.toString();
    }

    public abstract Component getClone();

    public abstract Domain getDomain(Variable variable);
}
