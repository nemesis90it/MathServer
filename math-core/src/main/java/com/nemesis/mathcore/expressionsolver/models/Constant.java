package com.nemesis.mathcore.expressionsolver.models;


/*

 */

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;

public class Constant extends Factor {

    public Constant(String number) {
        value = new BigDecimal(number);
    }

    public Constant(BigDecimal number) {
        value = number;
    }

    public Constant(Sign sign, BigDecimal value) {
        super.sign = sign;
        super.value = value;
    }

    @Override
    public String getDerivative() {
        return "0";
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
