package com.nemesis.mathcore.expressionsolver.components;


import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.stringbuilder.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.stringbuilder.LatexBuilder;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.utils.MathUtils;
import com.numericalmethod.suanshu.number.big.BigDecimalUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.DIVIDE;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.NEP_NUMBER;
import static java.math.BigDecimal.ONE;

@Data
public class Exponential extends Factor {

    private Base base;
    private Factor exponent;

    public Exponential(Base base, Factor exponent) {
        this.base = base;
        this.exponent = exponent;
    }

    public Exponential(Sign sign, Base base, Factor exponent) {
        super.sign = sign;
        this.base = base;
        this.exponent = exponent;
    }

    @Override
    public BigDecimal getValue() {

        BigDecimal exponentValue = exponent.getValue();

        if (exponentValue.compareTo(new BigDecimal(Integer.MIN_VALUE)) < 0) {
            throw new IllegalArgumentException("Exponent is too negative: " + exponentValue);
        }

        BigDecimal baseValue = base.getValue();
        if (baseValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Base must be positive: " + baseValue);
        }

        BigDecimal absValue;
        if (exponentValue.equals(BigDecimal.ZERO)) {
            absValue = BigDecimal.ONE;
        } else if (MathUtils.isIntegerValue(exponentValue)) {
            int exponentIntValue = exponentValue.intValueExact();
            if (exponentValue.compareTo(BigDecimal.ZERO) < 0) {
                absValue = MathUtils.divide(BigDecimal.ONE, baseValue.pow(-exponentIntValue));
            } else {
                absValue = baseValue.pow(exponentIntValue);
            }
        } else {
            if (exponentValue.compareTo(BigDecimal.ZERO) < 0) {
                absValue = MathUtils.divide(BigDecimal.ONE, BigDecimalUtils.pow(baseValue, exponentValue.multiply(MINUS_ONE_DECIMAL)));
            } else {
                absValue = BigDecimalUtils.pow(baseValue, exponentValue);
            }
        }
        value = sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE_DECIMAL);
        return value;
    }

    @Override
    public Component getDerivative(Variable var) {

        Component expDerivative = exponent.getDerivative(var);
        Component baseDerivative = base.getDerivative(var);

        Factor ed = Factor.getFactor(expDerivative);
        Term bd = Term.getTerm(baseDerivative);

        return new Term(
                this,
                MULTIPLY,
                new ParenthesizedExpression(
                        new Term(ed, MULTIPLY, new Logarithm(NEP_NUMBER, new ParenthesizedExpression(ComponentUtils.getExpression(base)))),
                        SUM,
                        new Term(
                                new ParenthesizedExpression(new Term(exponent, MULTIPLY, bd)),
                                DIVIDE,
                                base
                        ))
        );
    }

    @Override
    public Component rewrite(Rule rule) {
        this.setBase(ComponentUtils.getBase(this.getBase().rewrite(rule)));
        this.setExponent(Factor.getFactor(this.getExponent().rewrite(rule)));
        return rule.applyTo(this);
    }

    @Override
    public Boolean isScalar() {
        return this.base.isScalar() && this.exponent.isScalar();
    }

    @Override
    public boolean contains(Variable variable) {
        return base.contains(variable) || exponent.contains(variable);
    }

    @Override
    public int compareTo(Component c) {
        if (c instanceof Infinity i) {
            return i.getSign() == PLUS ? -1 : 1;
        } else if (c instanceof Exponential e) {
            Comparator<Exponential> baseComparator = Comparator.comparing(Exponential::getBase);
            // Exponential with greater constant degree will be shown from the left, decreasing
            final Comparator<Exponential> exponentComparator = Comparator.comparing(Exponential::getExponent).reversed();
            Comparator<Exponential> comparator = baseComparator.thenComparing(exponentComparator);
            return comparator.compare(this, e);
        } else if (c instanceof Base b) {
            return this.compareTo(new Exponential(b, new Constant(1)));
        } else {
            throw new UnsupportedOperationException("Comparison between [" + this.getClass() + "] and [" + c.getClass() + "] is not supported yet");
        }
    }

    @Override
    public Exponential getClone() {
        return new Exponential(this.sign, base.getClone(), exponent.getClone());
    }

    @Override
    public Domain getDomain(Variable variable) {
        Domain domain = new Domain();
        if (base.contains(variable)) {
            domain.addIntervals(base.getDomain(variable).getIntervals());
        }
        if (exponent.contains(variable)) {
            domain.addIntervals(exponent.getDomain(variable).getIntervals());
        }
        return domain;
    }

    @Override
    public Set<Variable> getVariables() {
        TreeSet<Variable> variables = new TreeSet<>();
        variables.addAll(base.getVariables());
        variables.addAll(exponent.getVariables());
        return variables;
    }

    @Override
    public String toString() {
        String baseAsString = base.toString();
        if (base instanceof ParenthesizedExpression) {
            baseAsString = "(" + baseAsString + ")";
        }
        String exponentAsString = exponent.toString();
        if (exponent instanceof ParenthesizedExpression) {
            exponentAsString = "(" + exponentAsString + ")";
        }
        return ExpressionBuilder.addSign(sign.toString(), ExpressionBuilder.power(baseAsString, exponentAsString));
    }

    @Override
    public String toLatex() {
        String baseAsLatex = base.toLatex();
        if (base instanceof ParenthesizedExpression) {
            baseAsLatex = "(" + baseAsLatex + ")";
        }
        String exponentAsLatex = "{" + exponent.toLatex() + "}";
        return LatexBuilder.addSign(sign.toString(), LatexBuilder.power(baseAsLatex, exponentAsLatex));
    }

    @Override
    public Classifier classifier() {
        return base.classifier();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Exponential that = (Exponential) o;
        return Objects.equals(base, that.base) &&
                Objects.equals(exponent, that.exponent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), base, exponent);
    }

    public static Exponential getExponential(Factor factor) {

        if (factor instanceof ParenthesizedExpression parExpr && parExpr.getOperator() == ExpressionOperator.NONE && parExpr.getTerm().getOperator() == TermOperator.NONE) {
            return getExponential(parExpr.getTerm().getFactor());
        }

        if (factor instanceof Base b) {
            return new Exponential(b, new Constant(ONE));
        }

        if (factor instanceof Exponential exponential) {
            return exponential;
        }

        throw new UnsupportedOperationException("Not implemented for type [" + factor.getClass() + "]");
    }
}

