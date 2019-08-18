package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE;

public class Factorial extends Factor {

    private Factor body;
    private Sign sign = Sign.PLUS;

    public Factorial(Factor body) {
        this.body = body;
    }

    public Factorial(Sign sign, Factor body) {
        this.body = body;
        this.sign = sign;
    }

    public Factor getBody() {
        return body;
    }

    public Sign getSign() {
        return sign;
    }

    @Override
    public BigDecimal getValue() {
        if (value == null) {
            BigDecimal bodyValue = body.getValue();
            String bodyValueAsString = bodyValue.toPlainString();
            if (bodyValueAsString.contains(".") || bodyValueAsString.startsWith("-")) {
                throw new IllegalArgumentException("Factorial must be a positive integer");
            }
            BigDecimal absValue = MathUtils.factorial(bodyValue);
            this.value = sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE);
        }
        return value;
    }
}
