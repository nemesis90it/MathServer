package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;

import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.NONE;

public class Monomial extends Polinomial {

    private final Constant coefficient;
    private final Base base;
    private final Factor exponent;

    public Monomial(Constant coefficient, Base base, Factor exponent) {
        this.coefficient = coefficient;
        this.base = base;
        this.exponent = exponent;
    }

    public static Monomial multiply(Monomial rightMonomial, Monomial leftMonomial) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public static Component divide(Monomial dividend, Monomial divisor) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public static Component power(Monomial base, Constant exponent) {
        // TODO
        throw new UnsupportedOperationException();
    }

    /* Monomial Tree
    ----------------------------FACTOR---------------------------
    --------------------------EXPRESSION-------------------------
    ----------------LEFT_TERM------------------    NONE      null
    -LEFT_FACT-  MUL  -----------TERM----------
                      -RIGHT_FACT-  NONE  null
     CONST       *     FACT
     CONST       *     EXPON
     FACT        *     CONST
     EXPON       *     CONST
 */
    public static Monomial getMonomial(Component component) {

        Term leftTerm;
        if (component instanceof ParenthesizedExpression) {
            ParenthesizedExpression expression = (ParenthesizedExpression) component;
            if (expression.getOperator().equals(ExpressionOperator.NONE)) {
                leftTerm = expression.getTerm();
            } else {
                return null;
            }
        } else {
            leftTerm = (Term) component;
        }

        if (leftTerm.getOperator().equals(MULTIPLY) && leftTerm.getSubTerm().getOperator().equals(NONE)) {
            Factor rightFactor = leftTerm.getSubTerm().getFactor();
            Factor leftFactor = leftTerm.getFactor();
            if (leftFactor instanceof Constant) {
                return buildMonomial((Constant) leftFactor, rightFactor);
            }
            if (rightFactor instanceof Constant) {
                return buildMonomial((Constant) rightFactor, leftFactor);
            }
        }

        return null;
    }

    private static Monomial buildMonomial(Constant constant, Component component) {
        if (component instanceof ParenthesizedExpression && ((ParenthesizedExpression) component).getOperator() == ExpressionOperator.NONE) {
            return null; // Factor cannot be a term
        }
        if (component instanceof Exponential) {
            Exponential rightFactorExponential = (Exponential) component;
            return new Monomial(constant, rightFactorExponential.getBase(), rightFactorExponential.getExponent());
        } else {
            return new Monomial(constant, (Base) component, new Constant("1"));
        }
    }
}