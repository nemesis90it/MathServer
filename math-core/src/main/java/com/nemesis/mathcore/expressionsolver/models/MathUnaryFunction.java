package com.nemesis.mathcore.expressionsolver.models;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;

import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;


public class MathUnaryFunction extends Factor {

    private UnaryOperator<BigDecimal> function;
    private Factor argument;

    public MathUnaryFunction(Sign sign, UnaryOperator<BigDecimal> function, Factor argument) {
        this.sign = sign;
        this.function = function;
        this.argument = argument;
    }

    public UnaryOperator<BigDecimal> getFunction() {
        return function;
    }

    public void setFunction(UnaryOperator<BigDecimal> function) {
        this.function = function;
    }

    public Factor getArgument() {
        return argument;
    }

    public void setArgument(Factor argument) {
        this.argument = argument;
    }

    @Override
    public BigDecimal getValue() {
        if (value == null) {
            value = function.apply(argument.getValue());
            value = sign.equals(PLUS) ? value : value.multiply(MINUS_ONE_DECIMAL);
        }
        return value;
    }
}
