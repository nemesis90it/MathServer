package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.utils.MathUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE;

public class Factorial extends Factor {

    private BigInteger integer;
    private Sign sign = Sign.PLUS;

    public Factorial(BigInteger integer) {
        this.integer = integer;
    }

    public Factorial(Sign sign, BigInteger integer) {
        this.integer = integer;
        this.sign = sign;
    }

    public BigInteger getInteger() {
        return integer;
    }

    public Sign getSign() {
        return sign;
    }

    @Override
    public BigDecimal getValue() {
        if (value == null) {
            BigDecimal absValue = MathUtils.factorial(new BigDecimal(integer));
            value = sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE);
        }
        return value;
    }
}
