package com.nemesis.mathcore.expressionsolver.expression.components;


import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;

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
    public Component getDerivative() {

        Component expDerivative = exponent.getDerivative();
        Component baseDerivative = base.getDerivative();
        Factor ed = expDerivative instanceof Term ? new ParenthesizedExpression((Term) expDerivative) : (Factor) expDerivative;
        Term bd = baseDerivative instanceof Term ? (Term) baseDerivative : new Term((Factor) baseDerivative);

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
        throw new UnsupportedOperationException();
    }


//    @Override
//    public Term simplify() {
//        return ExpressionBuilder.power(base.simplify(), exponent.simplify());
//    }

    @Override
    public String toString() {
        String absStr = base + "^" + exponent;
        if (sign == PLUS) {
            return absStr;
        } else {
            return sign + absStr;
        }
    }
}
