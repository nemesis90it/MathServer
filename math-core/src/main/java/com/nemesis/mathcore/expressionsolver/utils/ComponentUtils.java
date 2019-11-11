package com.nemesis.mathcore.expressionsolver.utils;


import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.Monomial;

import java.math.BigDecimal;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.NONE;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.DIVIDE;
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
            return buildTerm(coefficient, base, exponent, MULTIPLY);
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
        } else if (c instanceof Monomial) {
            return new Expression(getTerm(getTerm(c).rewrite(rule)));
        } else {
            throw new RuntimeException("Unexpected type [" + c.getClass() + "]");
        }
    }

    public static Expression applyConstantToExpression(Expression expr, Constant constant, TermOperator operator) {

        Term term = new Term(constant, operator, expr.getTerm());
        Term simplifiedTerm = ComponentUtils.getTerm(term.rewrite(rule));
        Expression result = new Expression(simplifiedTerm);

        if (!Objects.equals(expr.getOperator(), NONE)) {
            result.setOperator(expr.getOperator());
            result.setSubExpression(applyConstantToExpression(expr.getSubExpression(), constant, operator));
        }

        return result;
    }

    public static Factor cloneAndChangeSign(Factor factor) {
        Sign sign = factor.getSign().equals(MINUS) ? PLUS : MINUS;
        if (factor instanceof Logarithm) {
            return new Logarithm(sign, ((Logarithm) factor).getBase(), ((Logarithm) factor).getArgument());
        } else if (factor instanceof Variable) {
            return new Variable(sign, ((Variable) factor).getName());
        } else if (factor instanceof Constant) {
            BigDecimal value = factor.getValue();
            boolean isNegative = value.compareTo(BigDecimal.ZERO) < 0;
            Sign constantSign = isNegative ? MINUS : PLUS;
            sign = sign == constantSign ? PLUS : MINUS;
            if (sign == PLUS) {
                value = value.abs();
            }
            return new Constant(sign, value);
        } else if (factor instanceof Exponential) {
            return new Exponential(sign, ((Exponential) factor).getBase(), ((Exponential) factor).getExponent());
        } else if (factor instanceof ParenthesizedExpression) {
            return new ParenthesizedExpression(sign, ((ParenthesizedExpression) factor).getExpression());
        } else {
            // TODO
            throw new UnsupportedOperationException("Please implement it for class [" + factor.getClass() + "]");
        }
    }

    public static Term buildTerm(Constant coefficient, Base base, Factor exponent, TermOperator operator) {

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
                return new Term(coefficient, operator, new Term(base));
            }
        }
        if (exponent.getSign() == MINUS || (exponent instanceof Constant && exponent.getValue().compareTo(BigDecimal.ZERO) < 0)) {
            exponent = ComponentUtils.cloneAndChangeSign(exponent);
            return new Term(coefficient, DIVIDE, new Term(new Exponential(base, exponent)));
        }
        return new Term(coefficient, operator, new Term(new Exponential(base, exponent)));
    }

    public static boolean isFactor(Expression expr, Class<? extends Factor> c) {
        return expr.getOperator() == ExpressionOperator.NONE && expr.getTerm().getOperator() == TermOperator.NONE && expr.getTerm().getFactor().getClass().isAssignableFrom(c);
    }

}
