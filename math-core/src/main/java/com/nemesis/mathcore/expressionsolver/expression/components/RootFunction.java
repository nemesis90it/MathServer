package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.utils.ExponentialFunctions;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

public class RootFunction extends Factor {

    private final static BiFunction<BigDecimal, Integer, BigDecimal> nthRoot = ExponentialFunctions::nthRoot;

    private Integer rootIndex;
    private Factor argument;

    public RootFunction(Sign sign, Integer rootIndex, Factor argument) {
        this.rootIndex = rootIndex;
        this.argument = argument;
        super.sign = sign;
    }


    public Integer getRootIndex() {
        return rootIndex;
    }

    public void setRootIndex(Integer rootIndex) {
        this.rootIndex = rootIndex;
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
            value = nthRoot.apply(argument.getValue(), rootIndex);
            value = sign.equals(PLUS) ? value : value.multiply(MINUS_ONE_DECIMAL);
        }
        return value;
    }

    @Override
    public Component getDerivative() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Component simplify() {
        throw new UnsupportedOperationException();
    }
}
