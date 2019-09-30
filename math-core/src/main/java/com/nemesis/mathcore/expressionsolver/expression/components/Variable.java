package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;

public class Variable extends Base {

    public Variable(Sign sign) {
        super.sign = sign;
    }

    @Override
    public BigDecimal getValue() {
        throw new UnsupportedOperationException("Variables have no value");
    }

    @Override
    public Component getDerivative() {
        return new Constant("1");
    }

    @Override
    public Component simplify() {
        throw new UnsupportedOperationException();
    }

//    @Override
//    public String simplify() {
//        return this.toString();
//    }

    @Override
    public String toString() {
        if (sign.equals(PLUS)) {
            return "x";
        } else {
            return "(-x)";
        }
    }
}
