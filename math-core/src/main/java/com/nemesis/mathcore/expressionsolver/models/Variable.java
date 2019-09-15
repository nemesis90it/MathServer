package com.nemesis.mathcore.expressionsolver.models;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;

public class Variable extends Factor {
    public Variable(String number) {
        value = new BigDecimal(number);
    }

    public Variable(BigDecimal number) {
        value = number;
    }

    public Variable(Sign sign) {
        super.sign = sign;
    }

    @Override
    public String toString() {
        if (sign.equals(PLUS)) {
            return "" + value;
        } else {
            return "" + sign + value;
        }
    }
}
