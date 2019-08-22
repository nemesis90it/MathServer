package com.nemesis.mathcore.expressionsolver.models;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.NEP_NUMBER;

// TODO

public class Logarithm extends Factor {

    private BigDecimal base;
    private Factor argument;

    public Logarithm(Sign sign, BigDecimal base, Factor argument) {
        super();
        super.sign = sign;
        this.base = base;
        this.argument = argument;
    }

    public Logarithm(BigDecimal base, Factor argument) {
        super();
        this.base = base;
        this.argument = argument;
    }

    public BigDecimal getBase() {
        return base;
    }

    public Factor getArgument() {
        return argument;
    }


    @Override
    public BigDecimal getValue() {

        if (value == null) {
            BigDecimal absValue;
            if (base.equals(NEP_NUMBER)) {
                absValue = BigDecimal.valueOf(Math.log(argument.getValue().doubleValue()));
            } else if (base.equals(BigDecimal.TEN)) {
                absValue = BigDecimal.valueOf(Math.log10(argument.getValue().doubleValue()));
            } else {
                throw new UnsupportedOperationException("Logarithm base [" + base.toPlainString() + "] not supported");
            }
            this.value = sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE);
        }
        return value;
    }
}
