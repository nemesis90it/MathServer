package com.nemesis.mathcore.expressionsolver.components;


/*
         Factor ::= Number
         Factor ::= Exponential
         Factor ::= Factorial
         Factor ::= (Expression)
 */

import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.expressionsolver.utils.FactorMultiplier;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.NONE;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

public abstract class Factor extends Component {

    protected Sign sign = PLUS;

    @Override
    public BigDecimal getValue() {
        return value = sign.equals(PLUS) ? value : value.multiply(MINUS_ONE_DECIMAL);
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    /* Transform the given component in the simplest possible factor */
    public static Factor getFactor(Component c) {

        if (c instanceof AbsExpression absExpression) {
            return absExpression;
        }

        if (c instanceof ParenthesizedExpression parExpr && parExpr.getOperator() == NONE && parExpr.getTerm().getOperator() == TermOperator.NONE) {
            Factor factor = parExpr.getTerm().getFactor();
            if (parExpr.getSign() == MINUS) {
                return getFactor(ComponentUtils.cloneAndChangeSign(factor));
            } else {
                return getFactor(factor);
            }
        }

        if (c instanceof Expression expression) {
            if (expression.getOperator() == NONE && expression.getTerm().getOperator() == TermOperator.NONE) {
                return getFactor(expression.getTerm().getFactor());
            } else {
                return new ParenthesizedExpression((Expression) c);
            }
        }

        if (c instanceof Term term) {
            if (term.getOperator() == TermOperator.NONE) {
                return getFactor(term.getFactor());
            } else {
                return new ParenthesizedExpression(term);
            }
        }

        if (c instanceof Factor) {
            return (Factor) c;
        }

        throw new IllegalArgumentException("Unexpected type [" + c.getClass() + "]");

    }

    public static Factor getFactor(Sign sign, Term term) {
        Factor f = getFactor(term);
        f.setSign(sign);
        return f;
    }

    public static <T extends Factor> T getFactorOfSubtype(Component component, Class<T> c) {
        Factor factor = getFactor(component);
        if (c.isAssignableFrom(factor.getClass())) {
            return (T) factor;
        } else {
            return null;
        }
    }

    public static <T extends Factor> T getFactorOfSubtype(Sign sign, Component component, Class<T> c) {
        T f = getFactorOfSubtype(component, c);
        if (f != null) {
            f.setSign(sign);
        }
        return f;
    }


    public static boolean isFactorOfSubType(Component component, Class<? extends Factor> c) {
        return getFactorOfSubtype(component, c) != null;
    }

    public static Set<Factor> multiplyFactors(Set<Factor> inputFactors) {

        Set<Factor> outputFactors = new TreeSet<>();

        inputFactors.stream()
                .collect(Collectors.groupingBy(Factor::classifier))
                .forEach((classifier, factors) ->
                        outputFactors.add(FactorMultiplier.get(classifier.getFactorClass()).apply(factors))
                );

        return outputFactors;
    }

    /* Return a discriminator to determinate if two factors can be multiplied or simplified
     * E.g.:
     *       (x+1) and (x+1)^2
     *       log(10) and log(10)
     *       3 and 3^-1
     *  */
    public Classifier classifier() {
        return new Classifier(this.getClass());
    }

    @Data
    public static class Classifier {

        private Class<? extends Factor> factorClass;

        public Classifier(Class<? extends Factor> factorClass) {
            this.factorClass = factorClass;
        }

    }

    @Override
    public abstract Factor getClone();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Factor factor = (Factor) o;
        return sign == factor.sign;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sign);
    }
}
