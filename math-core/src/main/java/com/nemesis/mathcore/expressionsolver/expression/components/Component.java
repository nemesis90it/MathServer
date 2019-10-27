package com.nemesis.mathcore.expressionsolver.expression.components;

import java.math.BigDecimal;

public abstract class Component implements Comparable {

    BigDecimal value = null;

    public abstract BigDecimal getValue();

    public abstract Component getDerivative();

    public abstract Component simplify();

}
