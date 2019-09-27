package com.nemesis.mathcore.expressionsolver.models;


import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

public class Exponential extends Factor {

    private Factor base;
    private Factor exponent;

    public Exponential(Factor base, Factor exponent) {
        this.base = base;
        this.exponent = exponent;
    }

    public Exponential(Sign sign, Factor base, Factor exponent) {
        super.sign = sign;
        this.base = base;
        this.exponent = exponent;
    }

    public Factor getBase() {
        return base;
    }

    public Factor getExponent() {
        return exponent;
    }

    @Override
    public BigDecimal getValue() {

        if (value == null) {

            BigDecimal exponentValue = exponent.getValue();
            if (exponentValue.compareTo(new BigDecimal(Integer.MAX_VALUE)) > 0) {
                throw new IllegalArgumentException("Exponent is too large: " + exponentValue);
            }

            if (exponentValue.compareTo(new BigDecimal(Integer.MIN_VALUE)) < 0) {
                throw new IllegalArgumentException("Exponent is too negative: " + exponentValue);
            }

            BigDecimal baseValue = base.getValue();
            if (baseValue.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Base must be positive: " + baseValue);
            }

            BigDecimal absValue;
            if (exponentValue.equals(BigDecimal.ZERO)) {
                absValue = BigDecimal.ONE;
            } else {
                if (exponentValue.compareTo(BigDecimal.ZERO) < 0) {
                    absValue = MathUtils.divide(BigDecimal.ONE, baseValue.pow(-exponentValue.intValue()));
                } else {
                    absValue = baseValue.pow(exponentValue.intValue());
                }
            }
            value = sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE_DECIMAL);

        }
        return value;

    }

    @Override
    public Component getDerivative() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String simplify() {
        return ExpressionBuilder.power(base.simplify(), exponent.simplify());
    }

    @Override
    public String toString() {
        String absStr = base + "^" + exponent;
        if (sign == PLUS) {
            return absStr;
        } else {
            return sign + absStr;
        }
    }
}
