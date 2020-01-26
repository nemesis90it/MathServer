package com.nemesis.mathcore.expressionsolver.utils;


import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.Monomial;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.NONE;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;

public class ComponentUtils {

    public static Expression getExpression(Component c) {
        if (c instanceof Expression) {
            return (Expression) c;
        } else if (c instanceof Term) {
            return new Expression((Term) c);
        } else if (c instanceof Factor) {
            return new Expression(new Term((Factor) c));
        } else if (c instanceof Monomial) {
            return new Expression(Term.getSimplestTerm(c));
        } else {
            throw new RuntimeException("Unexpected type [" + c.getClass() + "]");
        }
    }

    public static Expression applyConstantToExpression(Expression expr, Constant constant, TermOperator operator) {

        Term term = new Term(constant, operator, expr.getTerm());
        Expression result = new Expression(Term.getSimplestTerm(ExpressionUtils.simplify(term)));

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

    public static Term applyTermOperator(Constant a, Constant b, TermOperator operator) {

        if (operator == TermOperator.NONE) {
            throw new IllegalArgumentException("Cannot apply operator " + TermOperator.NONE.name());
        }

        /* Apply operator to simple constant */

        if (a.getClass().equals(Constant.class) && b.getClass().equals(Constant.class)) {
            if (operator == MULTIPLY) {
                return new Term(getProduct(a, b));
            } else {
                return new Term(getQuotient(a, b));
            }
        }

        /* Apply operator to constant functions */

        boolean aIsComponentFunction = a.getClass().equals(ConstantFunction.class);
        boolean bIsComponentFunction = b.getClass().equals(ConstantFunction.class);
        if (aIsComponentFunction || bIsComponentFunction) {
            Component aComp = aIsComponentFunction ? ((ConstantFunction) a).getComponent() : a;
            Component bComp = bIsComponentFunction ? ((ConstantFunction) b).getComponent() : b;
            return new Term(Factor.getFactor(aComp), operator, Factor.getFactor(bComp));
        }

        /* Apply operator to fractions */

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
        return new Term(new Fraction(numerator, denominator));

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

    public static Base getBase(Component component) {
        Factor f = Factor.getFactor(component);
        return f instanceof Base ? (Base) f : new ParenthesizedExpression(f);
    }
}
