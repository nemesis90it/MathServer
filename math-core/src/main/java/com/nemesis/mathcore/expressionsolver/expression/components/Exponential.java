package com.nemesis.mathcore.expressionsolver.expression.components;


import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.DIVIDE;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.NEP_NUMBER;

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

    public Base getBase() {
        return base;
    }

    public Factor getExponent() {
        return exponent;
    }

    @Override
    public BigDecimal getValue() {

        if (value == null) {

            BigDecimal exponentValue = exponent.getValue();
            if (exponentValue.compareTo(new BigDecimal(Integer.MAX_VALUE)) > 0) {
                throw new IllegalArgumentException("Exponent is too large: " + exponentValue);
            }

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
            } else {
                if (exponentValue.compareTo(BigDecimal.ZERO) < 0) {
                    absValue = MathUtils.divide(BigDecimal.ONE, baseValue.pow(-exponentValue.intValue()));
                } else {
                    absValue = baseValue.pow(exponentValue.intValue());
                }
            }
            value = sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE_DECIMAL);

        }
        return value;

    }

    @Override
    public boolean absEquals(Object obj) {
        return obj instanceof Exponential &&
                Objects.equals(this.base, ((Exponential) obj).getBase()) &&
                Objects.equals(this.exponent, ((Exponential) obj).getExponent());
    }

    @Override
    public Component getDerivative() {

        Component expDerivative = exponent.getDerivative();
        Component baseDerivative = base.getDerivative();

        Factor ed = ComponentUtils.getFactor(expDerivative);
        Term bd = ComponentUtils.getTerm(baseDerivative);

        return new Term(
                this,
                MULTIPLY,
                new Term(new ParenthesizedExpression(
                        new Term(ed, MULTIPLY, new Term(new Logarithm(NEP_NUMBER, base))),
                        SUM,
                        new Expression(new Term(
                                new ParenthesizedExpression(new Term(exponent, MULTIPLY, bd)),
                                DIVIDE,
                                new Term(base)
                        ))
                ))
        );
    }

    @Override
    public Component simplify() {

        if (exponent instanceof Constant) {
            if (exponent.getValue().equals(BigDecimal.ONE)) {
                return base;
            }
            if (exponent.getValue().equals(BigDecimal.ZERO)) {
                return new Constant("1");
            }
        }

        // TODO
        throw new UnsupportedOperationException();
    }


    @Override
    public String toString() {
        return ExpressionBuilder.addSign(sign.toString(), ExpressionBuilder.power(base.toString(), exponent.toString()));
    }
}
