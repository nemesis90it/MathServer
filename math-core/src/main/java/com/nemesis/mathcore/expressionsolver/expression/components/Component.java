package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.math.BigDecimal;

public abstract class Component implements Comparable {

    BigDecimal value = null;

    public abstract BigDecimal getValue();

    public abstract Component getDerivative();

    public abstract Component rewrite(Rule rule);

}
