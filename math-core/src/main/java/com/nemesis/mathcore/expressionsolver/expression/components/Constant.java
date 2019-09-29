package com.nemesis.mathcore.expressionsolver.expression.components;


/*

 */

import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;

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
    public Constant getDerivative() {
        return new Constant("0");
    }

    @Override
    public Component simplify() {
        throw new UnsupportedOperationException();
    }

//    @Override
//    public Term simplify() {
//        if (value.equals(Constants.NEP_NUMBER)) {
//            return String.valueOf(Constants.E_CHAR);
//        }
//        if (value.equals(Constants.PI)) {
//            return String.valueOf(Constants.PI_CHAR);
//        }
//        return this.toString();
//    }

    @Override
    public String toString() {
        if (sign.equals(PLUS)) {
            return "" + value;
        } else {
            return "" + sign + value;
        }
    }
}
