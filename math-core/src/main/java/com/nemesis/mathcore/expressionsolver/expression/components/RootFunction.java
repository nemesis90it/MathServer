package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.models.GenericInterval;
import com.nemesis.mathcore.expressionsolver.models.RelationalOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.utils.ExponentialFunctions;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

@Data
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
        value = nthRoot.apply(argument.getValue(), rootIndex);
        value = sign.equals(PLUS) ? value : value.multiply(MINUS_ONE_DECIMAL);
        return value;
    }

    @Override
    public Component getDerivative(Variable var) {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Component rewrite(Rule rule) {
        throw new UnsupportedOperationException("Not implemented");
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
    public boolean contains(Variable variable) {
        return argument.contains(variable);
    }

    @Override
    public int compareTo(Component c) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RootFunction that = (RootFunction) o;
        return Objects.equals(rootIndex, that.rootIndex) &&
                Objects.equals(argument, that.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rootIndex, argument);
    }

    @Override
    public RootFunction getClone() {
        return new RootFunction(sign, rootIndex, argument.getClone());
    }

    @Override
    public Domain getDomain(Variable variable) {
        Domain domain = new Domain();
        if (argument.contains(variable)) {
            domain.addIntervals(argument.getDomain(variable).getIntervals());
            Set<GenericInterval> thisDefinitionSets = ExpressionUtils.resolve(this.argument, RelationalOperator.GREATER_THAN_OR_EQUALS, new Constant(0), variable);
            domain.addIntervals(thisDefinitionSets);
        }
        return domain;
    }

    @Override
    public Set<Variable> getVariables() {
        return argument.getVariables();
    }

    @Override
    public String toString() {
        String argumentAsString = argument.toString();
        return switch (rootIndex) {
            case 2 -> "√" + argumentAsString;
            case 3 -> "∛" + argumentAsString;
            case 4 -> "∜" + argumentAsString;
            default -> "root(" + rootIndex + "-th," + argumentAsString + ")";
        };
    }

    @Override
    public String toLatex() {
        return "\\sqrt[" + rootIndex + "]{" + argument.toLatex() + "}";
    }
}
