package com.nemesis.mathcore.expressionsolver.components;

import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.stringbuilder.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.stringbuilder.LatexBuilder;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.ComponentUtils.isWrappedExpression;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

@Data
@AllArgsConstructor
public class Fraction extends Constant {

    private Constant numerator;
    private Constant denominator;

    public Fraction(BigInteger numerator, BigInteger denominator) {
        this.numerator = new Constant(numerator);
        this.denominator = new Constant(denominator);
    }

    @Override
    public Boolean isScalar() {
        return true;
    }

    @Override
    public Constant getValueAsConstant() {
        return this;
    }

    @Override
    public BigDecimal getValue() {
        BigDecimal absValue = MathUtils.divide(numerator.getValue(), denominator.getValue());
        return sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE_DECIMAL);
    }

    @Override
    public Component rewrite(Rule rule) {
        this.numerator = (Constant) this.numerator.rewrite(rule);
        this.denominator = (Constant) this.denominator.rewrite(rule);
        return rule.applyTo(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Fraction fraction = (Fraction) o;
        return Objects.equals(numerator, fraction.numerator) &&
                Objects.equals(denominator, fraction.denominator);
    }

    @Override
    public Fraction getClone() {
        return new Fraction(numerator.getClone(), denominator.getClone());
    }

    @Override
    public String toString() {
        String numeratorAsString = numerator.toString();
        if (isWrappedExpression(numerator, ParenthesizedExpression.class)) {
            numeratorAsString = "(" + numeratorAsString + ")";
        } else if (isWrappedExpression(numerator, AbsExpression.class)) {
            numeratorAsString = "|" + numeratorAsString + "|";
        }

        String denominatorAsString = denominator.toString();
        if (isWrappedExpression(denominator, ParenthesizedExpression.class)) {
            denominatorAsString = "(" + denominatorAsString + ")";
        } else if (isWrappedExpression(denominator, AbsExpression.class)) {
            denominatorAsString = "|" + denominatorAsString + "|";
        }

        return ExpressionBuilder.division(numeratorAsString, denominatorAsString);
    }

    @Override
    public String toLatex() {
        String numeratorAsLatex = numerator.toLatex();
        if (isWrappedExpression(numerator, ParenthesizedExpression.class)) {
            numeratorAsLatex = "(" + numeratorAsLatex + ")";
        } else if (isWrappedExpression(numerator, AbsExpression.class)) {
            numeratorAsLatex = "|" + numeratorAsLatex + "|";
        }

        String denominatorAsLatex = denominator.toLatex();
        if (isWrappedExpression(denominator, ParenthesizedExpression.class)) {
            denominatorAsLatex = "(" + denominatorAsLatex + ")";
        } else if (isWrappedExpression(denominator, AbsExpression.class)) {
            denominatorAsLatex = "|" + denominatorAsLatex + "|";
        }

        return LatexBuilder.division(numeratorAsLatex, denominatorAsLatex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numerator, denominator);
    }
}
