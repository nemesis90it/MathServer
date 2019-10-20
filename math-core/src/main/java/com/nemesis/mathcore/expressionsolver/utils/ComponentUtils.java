package com.nemesis.mathcore.expressionsolver.utils;


import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.Monomial;

import java.math.BigDecimal;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;

public class ComponentUtils {

    public static Factor getFactor(Component c) {
        if (c instanceof Term) {
            return new ParenthesizedExpression((Term) c);
        } else if (c instanceof Factor) {
            return (Factor) c;
        } else if (c instanceof Expression) {
            return new ParenthesizedExpression((Expression) c);
        } else {
            throw new RuntimeException("Unexpected type [" + c.getClass() + "]");
        }
    }

    public static Term getTerm(Component c) {
        if (c instanceof Term) {
            return (Term) c;
        } else if (c instanceof Factor) {
            return new Term((Factor) c);
        } else if (c instanceof Expression) {
            return new Term(new ParenthesizedExpression((Expression) c));
        } else if (c instanceof Monomial) {
            Monomial m = (Monomial) c;

            Base base = m.getBase();
            Factor exponent = m.getExponent();
            Constant coefficient = m.getCoefficient();

            if (Objects.equals(exponent.getValue(), BigDecimal.ZERO)) {
                return new Term(new Constant("1"));
            }
            if (Objects.equals(coefficient.getValue(), BigDecimal.ZERO)) {
                return new Term(new Constant("0"));
            }
            if (Objects.equals(exponent.getValue(), BigDecimal.ONE)) {
                if (Objects.equals(coefficient.getValue(), BigDecimal.ONE)) {
                    return new Term(base);
                } else {
                    return new Term(coefficient, MULTIPLY, new Term(base));
                }
            }
            return new Term(coefficient, MULTIPLY, new Term(new Exponential(base, exponent)));
        } else {
            throw new RuntimeException("Unexpected type [" + c.getClass() + "]");
        }
    }

    public static Expression getExpression(Component c) {
        if (c instanceof Term) {
            return new Expression((Term) c);
        } else if (c instanceof Factor) {
            return new Expression(new Term((Factor) c));
        } else if (c instanceof Expression) {
            return (Expression) c;
        } else {
            throw new RuntimeException("Unexpected type [" + c.getClass() + "]");
        }
    }

    public static Expression applyConstantToExpression(Expression expr, Constant constant, TermOperator operator) {

        Term term = new Term(constant, operator, expr.getTerm());
        Term simplifiedTerm = ComponentUtils.getTerm(term.simplify());
        Expression result = new Expression(simplifiedTerm);

        if (!Objects.equals(expr.getOperator(), ExpressionOperator.NONE)) {
            result.setOperator(expr.getOperator());
            result.setSubExpression(applyConstantToExpression(expr.getSubExpression(), constant, operator));
        }

        return result;
    }
}
