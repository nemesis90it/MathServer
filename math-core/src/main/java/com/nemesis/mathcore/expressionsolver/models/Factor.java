package com.nemesis.mathcore.expressionsolver.models;


/*
         Factor ::= Number
         Factor ::= Exponential
         Factor ::= Factorial
         Factor ::= (Expression)
 */

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE;

public abstract class Factor {

    protected BigDecimal value = null;
    protected Sign sign = PLUS;

    public BigDecimal getValue() {
        return value = sign.equals(PLUS) ? value : value.multiply(MINUS_ONE);
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }
}
