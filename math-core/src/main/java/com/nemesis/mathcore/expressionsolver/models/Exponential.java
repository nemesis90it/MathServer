package com.nemesis.mathcore.expressionsolver.models;


import com.nemesis.mathcore.expressionsolver.utils.MathUtils;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE;

public class Exponential extends Factor {

    private Sign sign = PLUS;
    private Factor base;
    private Factor exponent;

    public Exponential(Factor base, Factor exponent) {
        this.base = base;
        this.exponent = exponent;
    }

    public Exponential(Sign sign, Factor base, Factor exponent) {
        this.sign = sign;
        this.base = base;
        this.exponent = exponent;
    }

    public Sign getSign() {
        return sign;
    }

    public Factor getBase() {
        return base;
    }

    public Factor getExponent() {
        return exponent;
    }


    @Override
    public BigDecimal getValue() {

        BigDecimal exponentValue = exponent.getValue();
        if (exponentValue.compareTo(new BigDecimal(Integer.MAX_VALUE)) > 0) {
            throw new ArithmeticException("Exponent is too large: " + exponentValue);
        }

        if (exponentValue.compareTo(new BigDecimal(Integer.MIN_VALUE)) < 0) {
            throw new ArithmeticException("Exponent is too negative: " + exponentValue);
        }

        if (this.value == null) {
            BigDecimal absValue;
            if (exponentValue.equals(BigDecimal.ZERO)) {
                absValue = BigDecimal.ONE;
            } else if (exponentValue.compareTo(BigDecimal.ZERO) < 0) {
                absValue = MathUtils.divide(BigDecimal.ONE, base.getValue().pow(-exponentValue.intValue()));
            } else {
                absValue = base.getValue().pow(exponentValue.intValue());
            }
            this.value = sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE);

        }
        return this.value;

    }

}
