package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

public class Factorial extends Factor {

    private Factor body;

    public Factorial(Factor body) {
        this.body = body;
    }

    public Factorial(Sign sign, Factor body) {
        super.sign = sign;
        this.body = body;
    }

    public Factor getBody() {
        return body;
    }

    @Override
    public BigDecimal getValue() {
        if (value == null) {
            BigDecimal bodyValue = body.getValue();
            bodyValue = SyntaxUtils.removeNonSignificantZeros(bodyValue);
            String bodyValueAsString = bodyValue.toPlainString();
            if (bodyValueAsString.contains(".") || bodyValueAsString.startsWith("-")) {
                throw new IllegalArgumentException("Factorial must be a positive integer");
            }
            BigDecimal absValue = MathUtils.factorial(bodyValue);
            this.value = sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE_DECIMAL);
        }
        return value;
    }

    @Override
    public String toString() {
        if (sign.equals(PLUS)) {
            return "(" + body + ")!";
        } else {
            return sign + "(" + body + ")!";
        }
    }
}
