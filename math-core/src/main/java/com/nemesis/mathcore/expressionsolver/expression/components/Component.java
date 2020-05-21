package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
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
}
