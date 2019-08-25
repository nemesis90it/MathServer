package com.nemesis.mathcore.expressionsolver.models;


/*

 */

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;

public class Number extends Factor {

    public Number(String number) {
        value = new BigDecimal(number);
    }

    public Number(BigDecimal number) {
        value = number;
    }

    public Number(Sign sign, BigDecimal value) {
        super.sign = sign;
        super.value = value;
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
