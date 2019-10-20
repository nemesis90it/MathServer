package com.nemesis.mathcore.expressionsolver.utils;


import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.Monomial;

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
            return new Term(m.getCoefficient(), MULTIPLY, new Term(new Exponential(m.getBase(), m.getExponent())));
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
