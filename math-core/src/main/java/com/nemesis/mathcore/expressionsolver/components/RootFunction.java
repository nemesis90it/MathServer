package com.nemesis.mathcore.expressionsolver.components;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.models.intervals.GenericInterval;
import com.nemesis.mathcore.expressionsolver.models.RelationalOperator;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.Constants;
import com.nemesis.mathcore.utils.ExponentialFunctions;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

@Data
public class RootFunction extends MathFunction {

    private final static BiFunction<BigDecimal, Integer, BigDecimal> nthRoot = ExponentialFunctions::nthRoot;

    private Integer rootIndex;
    private Factor argument;

    public RootFunction(Integer rootIndex, Factor argument) {
        this.rootIndex = rootIndex;
        this.argument = argument;
        super.sign = PLUS;
    }

    public RootFunction(Sign sign, Integer rootIndex, Factor argument) {
        this.rootIndex = rootIndex;
        this.argument = argument;
        super.sign = sign;
    }

    @Override
    public BigDecimal getValue() {
        if (rootIndex > 2) {
            value = nthRoot.apply(argument.getValue(), rootIndex);
        } else {
            value = argument.getValue().sqrt(Constants.MATH_CONTEXT);
        }
        value = sign.equals(PLUS) ? value : value.multiply(MINUS_ONE_DECIMAL);
        return value;
    }

    @Override
    public Component getDerivative(Variable var) {
        final Term rootFunctionDerivative = new Term(new Constant(1),
                TermOperator.DIVIDE,
                new Term(new Constant(rootIndex),
                        MULTIPLY,
                        new Exponential(new RootFunction(rootIndex, argument), new ParenthesizedExpression(new Constant(rootIndex), SUBTRACT, new Constant(1)))
                )
        );

        final Component argumentDerivative = this.argument.getDerivative(var);

        return new Term(rootFunctionDerivative, MULTIPLY, argumentDerivative);
    }

    @Override
    public Component rewrite(Rule rule) {
        this.setArgument(Factor.getFactor(this.getArgument().rewrite(rule)));
        return rule.applyTo(this);
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
    public int compareTo(Component c) {
        if (c instanceof RootFunction rf) {
            Comparator<RootFunction> indexComparator = Comparator.comparing(RootFunction::getRootIndex);
            Comparator<RootFunction> rootFunctionComparator = indexComparator.thenComparing(RootFunction::getArgument);
            return rootFunctionComparator.compare(this, rf);
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
        RootFunction that = (RootFunction) o;
        return Objects.equals(rootIndex, that.rootIndex) &&
                Objects.equals(argument, that.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rootIndex, argument);
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
            Set<GenericInterval> thisDefinitionSets = ExpressionUtils.resolve(this.argument, RelationalOperator.GTE, new Constant(0), variable);
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
        String argumentAsString;
        if (argument instanceof AbsExpression absExpression) {
            argumentAsString = "|" + argument.toString() + "|";
        } else if (argument instanceof ParenthesizedExpression parExpr) {
            argumentAsString = "(" + argument.toString() + ")";
        } else {
            argumentAsString = argument.toString();
        }

        String signChar = sign == PLUS ? "" : "-";

        return switch (rootIndex) {
            case 2 -> signChar + "√" + argumentAsString;
            case 3 -> signChar + "∛" + argumentAsString;
            case 4 -> signChar + "∜" + argumentAsString;
            default -> signChar + "root(" + rootIndex + "-th," + argumentAsString + ")";
        };
    }

    @Override
    public String toLatex() {
        String argumentAsLatex;
        if (argument instanceof AbsExpression absExpression) {
            argumentAsLatex = "|" + argument.toLatex() + "|";
        } else if (argument instanceof ParenthesizedExpression parExpr) {
            argumentAsLatex = "(" + argument.toLatex() + ")";
        } else {
            argumentAsLatex = argument.toLatex();
        }
        final String indexAsLatex = rootIndex == 2 ? "" : "[" + rootIndex + "]";
        String signChar = sign == PLUS ? "" : "-";
        return signChar + "\\sqrt" + indexAsLatex + "{" + argumentAsLatex + "}";
    }
}
