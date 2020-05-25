package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.LatexBuilder;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

@Data
@AllArgsConstructor
public class Fraction extends Constant {

    private Constant numerator;
    private Constant denominator;

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
        return ExpressionBuilder.division(numerator.toString(), denominator.toString());
    }

    @Override
    public String toLatex() {
        return LatexBuilder.division(numerator.toLatex(), denominator.toLatex());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numerator, denominator);
    }
}
