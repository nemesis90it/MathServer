package com.nemesis.mathcore.expressionsolver.expression.components;


import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Comparator;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.DIVIDE;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.NEP_NUMBER;

@EqualsAndHashCode(callSuper = false)
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
                int exponentIntValue = exponentValue.intValueExact();
                if (exponentValue.compareTo(BigDecimal.ZERO) < 0) {
                    absValue = MathUtils.divide(BigDecimal.ONE, baseValue.pow(-exponentIntValue));
                } else {
                    absValue = baseValue.pow(exponentIntValue);
                }
            }
            value = sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE_DECIMAL);

        }
        return value;

    }

    @Override
    public Component getDerivative(char var) {

        Component expDerivative = exponent.getDerivative(var);
        Component baseDerivative = base.getDerivative(var);

        Factor ed = ComponentUtils.getFactor(expDerivative);
        Term bd = ComponentUtils.getTerm(baseDerivative);

        return new Term(
                this,
                MULTIPLY,
                new ParenthesizedExpression(
                        new Term(ed, MULTIPLY, new Logarithm(NEP_NUMBER, ComponentUtils.getExpression(base))),
                        SUM,
                        new Expression(new Term(
                                new ParenthesizedExpression(new Term(exponent, MULTIPLY, bd)),
                                DIVIDE,
                                base
                        ))
                )
        );
    }

    @Override
    public Component rewrite(Rule rule) {
        this.setBase((Base) ComponentUtils.getFactor(this.getBase().rewrite(rule)));
        this.setExponent(ComponentUtils.getFactor(this.getExponent().rewrite(rule)));
        return rule.applyTo(this);
    }

    @Override
    public Boolean isScalar() {
        return this.base.isScalar() && this.exponent.isScalar();
    }

    @Override
    public Constant getValueAsConstant() {
        return new Constant(this.getValue());
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Exponential) {
            Comparator<Exponential> baseComparator = Comparator.comparing(Exponential::getBase);
            Comparator<Exponential> comparator = baseComparator.thenComparing(Exponential::getExponent);
            return comparator.compare(this, (Exponential) o);
        } else {
            return Base.compare(this, o);
        }
    }

    @Override
    public String toString() {
        String baseAsString = base.toString();
        if (base instanceof ParenthesizedExpression) {
            baseAsString = "(" + baseAsString + ")";
        }
        return ExpressionBuilder.addSign(sign.toString(), ExpressionBuilder.power(baseAsString, exponent.toString()));
    }
}

