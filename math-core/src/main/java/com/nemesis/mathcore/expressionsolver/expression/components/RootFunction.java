package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.utils.ExponentialFunctions;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

@Data
@EqualsAndHashCode(callSuper = false)
public class RootFunction extends MathFunction {

    private final static BiFunction<BigDecimal, Integer, BigDecimal> nthRoot = ExponentialFunctions::nthRoot;

    private Integer rootIndex;
    private Factor argument;

    public RootFunction(Sign sign, Integer rootIndex, Factor argument) {
        this.rootIndex = rootIndex;
        this.argument = argument;
        super.sign = sign;
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
    public Component getDerivative(char var) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Component rewrite(Rule rule) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean isScalar() {
        return this.argument.isScalar();
    }

    @Override
    public Constant getValueAsConstant() {
        return new Constant(this.getValue());
    }

    @Override
    public int compareTo(Object o) {
        throw new UnsupportedOperationException();
    }
}
