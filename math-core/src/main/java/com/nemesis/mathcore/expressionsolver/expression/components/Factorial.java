package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.models.EquationOperator;
import com.nemesis.mathcore.expressionsolver.models.interval.GenericInterval;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

@Data
public class Factorial extends Base {

    private Factor argument;

    public Factorial(Factor argument) {
        this.argument = argument;
    }

    public Factorial(Sign sign, Factor argument) {
        super.sign = sign;
        this.argument = argument;
    }

    @Override
    public BigDecimal getValue() {
        BigDecimal bodyValue = argument.getValue();
        bodyValue = SyntaxUtils.removeNonSignificantZeros(bodyValue);
        String bodyValueAsString = bodyValue.toPlainString();
        if (bodyValueAsString.contains(".") || bodyValueAsString.startsWith("-")) {
            throw new IllegalArgumentException("Factorial must be a positive integer");
        }
        BigDecimal absValue = MathUtils.factorial(bodyValue);
        this.value = sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE_DECIMAL);
        return value;
    }

    @Override
    public Component getDerivative(char var) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Component rewrite(Rule rule) {

        Component simplifiedArg = argument.rewrite(rule);

        if (simplifiedArg instanceof Constant && simplifiedArg.getValue().equals(BigDecimal.ZERO)) {
            return new Constant("1");
        }

        return new Factorial(Factor.getFactor(simplifiedArg));
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
    public Factorial getClone() {
        return new Factorial(this.sign, argument.getClone());
    }

    @Override
    public Domain getDomain(Variable variable) {
        Domain domain = new Domain();
        if (argument.contains(variable)) {
            domain.addIntervals(argument.getDomain(variable).getIntervals());
            Set<GenericInterval> thisDefinitionSets = ExpressionUtils.resolve(this.argument, EquationOperator.GREATER_THAN_OR_EQUALS, new Constant(0), variable);
            domain.addIntervals(thisDefinitionSets);
        }
        return domain;
    }

    @Override
    public String toString() {
        return this.argument + "!";
    }

    @Override
    public String toLatex() {
        return this.argument.toLatex() + "!";
    }

    @Override
    public int compareTo(Component c) {
        if (c instanceof Factorial f) {
            Comparator<Factorial> argComparator = Comparator.comparing(Factorial::getArgument);
            return argComparator.compare(this, f);
        } else if (c instanceof Base b) {
            return Base.compare(this, b);
        } else if (c instanceof Exponential e) {
            return new Exponential(this, new Constant(1)).compareTo(e);
        } else {
            throw new UnsupportedOperationException("Comparison between [" + this.getClass() + "] and [" + c.getClass() + "] is not supported yet");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Factorial factorial = (Factorial) o;
        return Objects.equals(argument, factorial.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument);
    }
}
