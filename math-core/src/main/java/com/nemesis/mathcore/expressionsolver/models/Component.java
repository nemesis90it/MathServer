package com.nemesis.mathcore.expressionsolver.models;

import java.math.BigDecimal;

public abstract class Component {

    BigDecimal value = null;

    public abstract BigDecimal getValue();

    public abstract Component getDerivative();

    public abstract String simplify();
}
