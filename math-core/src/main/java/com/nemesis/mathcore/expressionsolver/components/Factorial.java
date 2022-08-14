package com.nemesis.mathcore.expressionsolver.components;

import com.nemesis.mathcore.expressionsolver.utils.EquationUtils;
import com.nemesis.mathcore.expressionsolver.exception.UnexpectedComponentTypeException;
import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.models.Equation;
import com.nemesis.mathcore.expressionsolver.models.RelationalOperator;
import com.nemesis.mathcore.expressionsolver.intervals.model.GenericInterval;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.stringbuilder.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_INTEGER;

@Data
public class Factorial extends Base {

    private Base argument;

    public Factorial(Base argument) {
        this.argument = argument;
    }

    public Factorial(Sign sign, Base argument) {
        super.sign = sign;
        this.argument = argument;
    }

    @Override
    public BigDecimal getValue() {
        BigDecimal argumentValue = argument.getValue();
        if (argumentValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        }
        argumentValue = SyntaxUtils.removeNonSignificantZeros(argumentValue);
        String bodyValueAsString = argumentValue.toPlainString();
        if (bodyValueAsString.contains(".") || bodyValueAsString.startsWith("-")) {
            throw new IllegalArgumentException("Factorial must be a positive integer");
        }
        BigInteger absValue = MathUtils.factorial(argumentValue.toBigInteger());
        this.value = new BigDecimal(sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE_INTEGER));
        return value;
    }

    @Override
    public Component getDerivative(Variable var) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Component rewrite(Rule rule) {

        Component simplifiedArg = argument.rewrite(rule);

        if (simplifiedArg instanceof Constant && simplifiedArg.getValue().equals(BigDecimal.ZERO)) {
            return new Constant("1");
        }

        return new Factorial(getFactorOfSubtype(simplifiedArg, Base.class));
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
            domain.intersectWith(argument.getDomain(variable).getIntervals());
            Equation domainEquation = new Equation(this.argument, RelationalOperator.GTE, new Constant(0));
            Set<GenericInterval> thisDefinitionSets = EquationUtils.resolve(domainEquation, variable);
            domain.intersectWith(thisDefinitionSets);
        }
        return domain;
    }

    @Override
    public Set<Variable> getVariables() {
        return argument.getVariables();
    }

    @Override
    public String toString() {
        String argumentAsString;
        if (argument instanceof WrappedExpression) {
            if (argument instanceof ParenthesizedExpression) {
                argumentAsString = ExpressionBuilder.toParenthesized(argument.toString());
            } else if (argument instanceof AbsExpression) {
                argumentAsString = ExpressionBuilder.toAbsExpression(argument.toString());
            } else {
                throw new UnexpectedComponentTypeException("Unexpected wrapped expression type [" + argument.getClass() + "]");
            }
        } else {
            argumentAsString = argument.toString();
        }
        return argumentAsString + "!";
    }

    @Override
    public String toLatex() {
        String argumentAsString;
        if (argument instanceof WrappedExpression) {
            if (argument instanceof ParenthesizedExpression) {
                argumentAsString = ExpressionBuilder.toParenthesized(argument.toLatex());
            } else if (argument instanceof AbsExpression) {
                argumentAsString = ExpressionBuilder.toAbsExpression(argument.toLatex());
            } else {
                throw new UnexpectedComponentTypeException("Unexpected wrapped expression type [" + argument.getClass() + "]");
            }
        } else {
            argumentAsString = argument.toLatex();
        }
        return argumentAsString + "!";

    }

    @Override
    public int compareTo(Component c) {
        if (c instanceof Infinity i) {
            return i.getSign() == PLUS ? -1 : 1;
        } else if (c instanceof Factorial f) {
            Comparator<Factorial> argComparator = Comparator.comparing(Factorial::getArgument);
            return argComparator.compare(this, f);
        } else if (c instanceof Base b) {
            return compare(this, b);
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
        if (!super.equals(o)) return false;
        Factorial factorial = (Factorial) o;
        return Objects.equals(argument, factorial.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), argument);
    }
}
