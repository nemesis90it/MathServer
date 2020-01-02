package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.NONE;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class Fraction extends Constant {

    private Constant numerator;
    private Constant denominator;

    @Override
    public String toString() {
        return ExpressionBuilder.division(numerator.toString(), denominator.toString());
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

    public static Constant applyTermOperator(Constant a, Constant b, TermOperator operator) {

        if (operator == NONE) {
            throw new IllegalArgumentException("Cannot apply operator " + NONE.name());
        }

        if (!(a instanceof Fraction) && !(b instanceof Fraction)) {
            if (operator == MULTIPLY) {
                return getProduct(a, b);
            } else {
                return getQuotient(a, b);
            }
        }

        Fraction af;
        if (a instanceof Fraction) {
            af = (Fraction) a;
        } else {
            af = new Fraction(a, new Constant("1"));
        }

        Fraction bf;
        if (b instanceof Fraction) {
            bf = (Fraction) b;
        } else {
            bf = new Fraction(new Constant("1"), b);
        }

        Constant numerator;
        Constant denominator;
        if (operator == MULTIPLY) {
            numerator = getProduct(af.getNumerator(), bf.getNumerator());
            denominator = getProduct(af.getDenominator(), bf.getDenominator());
        } else {
            numerator = getProduct(af.getNumerator(), bf.getDenominator());
            denominator = getProduct(af.getDenominator(), bf.getNumerator());
        }
        return new Fraction(numerator, denominator);

    }


    private static Constant getProduct(Constant a, Constant b) {
        return new Constant(a.getValue().multiply(b.getValue()));
    }

    private static Constant getQuotient(Constant a, Constant b) {
        BigDecimal quotient = MathUtils.divide(a.getValue(), (b.getValue()));
        if (!MathUtils.isIntegerValue(quotient)) {
            return new Fraction(a, b);
        } else {
            return new Constant(quotient);
        }
    }

}
