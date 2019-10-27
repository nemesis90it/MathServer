package com.nemesis.mathcore.expressionsolver.expression.components;


/*
         Factor ::= Number
         Factor ::= Exponential
         Factor ::= Factorial
         Factor ::= (Expression)
 */

import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

public abstract class Factor extends Component {

    protected Sign sign = PLUS;

    @Override
    public BigDecimal getValue() {
        return value = sign.equals(PLUS) ? value : value.multiply(MINUS_ONE_DECIMAL);
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public abstract boolean absEquals(Object obj);
}
