package com.nemesis.mathcore.expressionsolver.components;

import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;

import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
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
        value = function.apply(argument.getValue());
        value = sign.equals(PLUS) ? value : value.multiply(MINUS_ONE_DECIMAL);
        return value;
    }

    @Override
    public Component getDerivative(Variable var) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Component rewrite(Rule rule) {
        return this;
        // TODO
//        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Boolean isScalar() {
        return this.argument.isScalar();
    }

    @Override
    public boolean contains(Variable variable) {
        return argument.contains(variable);
    }

    @Override
    public MathUnaryFunction getClone() {
//        UnaryOperator<BigDecimal> functionClone = arg -> function.apply(arg); // TODO: this is may be not a real cloning, test it
        return new MathUnaryFunction(this.sign, function, functionName, argument.getClone());
    }

    @Override
    public Domain getDomain(Variable variable) {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Set<Variable> getVariables() {
        return argument.getVariables();
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
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MathUnaryFunction that = (MathUnaryFunction) o;
        return Objects.equals(function, that.function) &&
                Objects.equals(argument, that.argument) &&
                Objects.equals(functionName, that.functionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), function, argument, functionName);
    }
}
