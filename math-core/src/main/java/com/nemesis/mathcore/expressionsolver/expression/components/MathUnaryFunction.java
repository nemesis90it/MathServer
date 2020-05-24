package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.UnaryOperator;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;


public class MathUnaryFunction extends MathFunction {

    private UnaryOperator<BigDecimal> function;
    private Factor argument;
    private String functionName;

    public MathUnaryFunction(Sign sign, UnaryOperator<BigDecimal> function, String functionName, Factor argument) {
        this.sign = sign;
        this.function = function;
        this.functionName = functionName;
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

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public BigDecimal getValue() {
        if (value == null) {
            value = function.apply(argument.getValue());
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
        return this;
        // TODO
//        throw new UnsupportedOperationException();
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
    public String toString() {
        String signChar = sign.equals(MINUS) ? "-" : "";
        return signChar + functionName + "(" + argument + ")";
    }

    @Override
    public String toLatex() {
        String signChar = sign.equals(MINUS) ? "-" : "";
        return signChar + functionName + "(" + argument.toLatex() + ")";
    }

    @Override
    public int compareTo(Component c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MathUnaryFunction that = (MathUnaryFunction) o;
        return Objects.equals(function, that.function) &&
                Objects.equals(argument, that.argument) &&
                Objects.equals(functionName, that.functionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, argument, functionName);
    }
}
