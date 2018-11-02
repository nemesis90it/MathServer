package com.nemesis.mathcore.expressionsolver.models;


/*
         Factor ::= Number
         Factor ::= Exponential
         Factor ::= Factorial
         Factor ::= (Expression)
 */

import java.math.BigDecimal;

public abstract class Factor {

    protected BigDecimal value = null;

    public BigDecimal getValue() {
        return value;
    }
}
