package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.*;

public class Monomial extends Polinomial {

    private final Constant coefficient;
    private final Base base;
    private final Factor exponent;

    public Monomial(Constant coefficient, Base base, Factor exponent) {
        this.coefficient = coefficient;
        this.base = base;
        this.exponent = exponent;
    }

    public Constant getCoefficient() {
        return coefficient;
    }

    public Base getBase() {
        return base;
    }

    public Factor getExponent() {
        return exponent;
    }

    public static Term sum(Monomial rightMonomial, Monomial leftMonomial) {
        return sumOrSubtract(rightMonomial, leftMonomial, BigDecimal::add);
    }

    public static Term subtract(Monomial rightMonomial, Monomial leftMonomial) {
        return sumOrSubtract(rightMonomial, leftMonomial, BigDecimal::subtract);
    }

    public static Term multiply(Monomial leftMonomial, Monomial rightMonomial) {
        return multiplyOrDivide(leftMonomial, rightMonomial, BigDecimal::multiply, MULTIPLY, SUM);
    }

    public static Term divide(Monomial dividend, Monomial divisor) {
        if (divisor.getCoefficient().getValue().equals(BigDecimal.ZERO)) {
            if (dividend.getCoefficient().getValue().equals(BigDecimal.ZERO)) {
                throw new ArithmeticException("Cannot divide zero by zero");
            } else {
                // TODO: return INFINITY
                throw new ArithmeticException("Division by zero is not supported yet");
            }
        }
        return multiplyOrDivide(dividend, divisor, MathUtils::divide, DIVIDE, SUBTRACT);
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
        } else if (component instanceof Expression) {
            Expression expression = (Expression) component;
            if (expression.getOperator().equals(ExpressionOperator.NONE)) {
                leftTerm = expression.getTerm();
            } else {
                return null;
            }
        } else if (component instanceof Factor) {
            leftTerm = new Term((Factor) component);
        } else if (component instanceof Term) {
            leftTerm = (Term) component;
        } else {
            throw new RuntimeException("Unexpected type [" + component.getClass() + "]");
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
        } else if (leftTerm.getOperator().equals(NONE)) {
            Factor factor = leftTerm.getFactor();
            if (factor instanceof Constant) {
                return buildMonomial((Constant) factor, null);
            }
            return buildMonomial(new Constant("1"), factor);
        }

        return null;
    }

    private static Monomial buildMonomial(Constant constant, Component component) {

        if (component == null) {
            return new Monomial(constant, null, new Constant("1"));
        }

        if (component instanceof ParenthesizedExpression && ((ParenthesizedExpression) component).getOperator() == ExpressionOperator.NONE) {
            return null; // Factor cannot be a term
        }
        if (component instanceof Exponential) {
            Exponential rightFactorExponential = (Exponential) component;
            return new Monomial(constant, rightFactorExponential.getBase(), rightFactorExponential.getExponent());
        } else if (component instanceof Base) {
            return new Monomial(constant, (Base) component, new Constant("1"));
        } else {
            throw new RuntimeException("Unexpected type [" + component.getClass() + "]");
        }
    }

    private static Term sumOrSubtract(Monomial rightMonomial, Monomial leftMonomial, BiFunction<BigDecimal, BigDecimal, BigDecimal> function) {

        if (leftMonomial == null && rightMonomial == null) {
            return null;
        }

        if (rightMonomial == null) {
            if (leftMonomial.getBase() == null) {
                return new Term(leftMonomial.getCoefficient());
            } else {
                return new Term(leftMonomial.getCoefficient(), MULTIPLY, new Term(new Exponential(leftMonomial.getBase(), leftMonomial.getExponent())));
            }
        } else if (leftMonomial == null) {
            if (rightMonomial.getBase() == null) {
                return new Term(rightMonomial.getCoefficient());
            } else {
                return new Term(rightMonomial.getCoefficient(), MULTIPLY, new Term(new Exponential(rightMonomial.getBase(), rightMonomial.getExponent())));
            }
        }

        Constant coefficient = new Constant(function.apply(rightMonomial.getCoefficient().getValue(), leftMonomial.getCoefficient().getValue()));

        if (leftMonomial.getBase() == null && rightMonomial.getBase() == null) {
            return new Term(coefficient);
        }

        if (leftMonomial.getBase() == null || rightMonomial.getBase() == null) {
            return null;
        }

        if (!rightMonomial.getBase().absEquals(leftMonomial.getBase()) || !rightMonomial.getExponent().absEquals(leftMonomial.getExponent())) {
            return null;
        }

        Base base = rightMonomial.getBase();
        Factor exponent = rightMonomial.getExponent();

        return new Term(coefficient, MULTIPLY, new Term(new Exponential(base, exponent)));
    }

    /*
        If 'x' is the base:
            a+x^c * b*x^d   =>     (a*b)*x^(c+d)
            a+x^c / b*x^d   =>     (a/b)*x^(c-d)
     */
    private static Term multiplyOrDivide(Monomial leftMonomial, Monomial rightMonomial, BiFunction<BigDecimal, BigDecimal,
            BigDecimal> function, TermOperator operator, ExpressionOperator exponentOperator) {

        if (leftMonomial == null && rightMonomial == null) {
            return null;
        }

        if (rightMonomial == null) {
            if (leftMonomial.getBase() == null) {
                return new Term(leftMonomial.getCoefficient()); // b
            } else {
                return new Term(leftMonomial.getCoefficient(), operator, new Term(new Exponential(leftMonomial.getBase(), leftMonomial.getExponent()))); // b OP x^d
            }
        } else if (leftMonomial == null) {
            if (rightMonomial.getBase() == null) {
                return new Term(rightMonomial.getCoefficient()); // a
            } else {
                return new Term(rightMonomial.getCoefficient(), operator, new Term(new Exponential(rightMonomial.getBase(), rightMonomial.getExponent()))); // a OP x^c
            }
        }

        Constant coefficient = new Constant(function.apply((leftMonomial.getCoefficient().getValue()), rightMonomial.getCoefficient().getValue()));

        if (coefficient.getValue().equals(BigDecimal.ZERO)) {
            return new Term(coefficient); // a OP b
        }

        if (rightMonomial.getBase() == null && leftMonomial.getBase() == null) {
            return new Term(coefficient); // a OP b
        }

        Base base;
        Factor exponent;

        if (leftMonomial.getBase() == null) {
            base = rightMonomial.getBase();
            exponent = rightMonomial.getExponent();
            return new Term(coefficient, operator, new Term(new Exponential(base, exponent))); // (a OP b)*x^d
        }

        if (rightMonomial.getBase() == null) {
            base = leftMonomial.getBase();
            exponent = leftMonomial.getExponent();
            return new Term(coefficient, operator, new Term(new Exponential(base, exponent))); // (a OP b)*x^c
        }

        if (!rightMonomial.getBase().absEquals(leftMonomial.getBase())) {
            return null;
        }

        base = rightMonomial.getBase(); // Can be used the left monomial, it is the same

        Component exponentComponent = new ParenthesizedExpression(new Term(rightMonomial.getExponent()), exponentOperator, new Expression(new Term(leftMonomial.getExponent()))).simplify();

        Function<Term, Factor> termToExponent = term -> {
            if (term.getOperator().equals(NONE)) {
                return term.getFactor();
            } else {
                return new ParenthesizedExpression(term);
            }
        };

        if (exponentComponent instanceof Factor) {
            exponent = (Factor) exponentComponent;
        } else if (exponentComponent instanceof Term) {
            exponent = termToExponent.apply((Term) exponentComponent);
        } else if (exponentComponent instanceof Expression) {
            Expression exponentComponentAsExpression = (Expression) exponentComponent;
            if (exponentComponentAsExpression.getOperator().equals(ExpressionOperator.NONE)) {
                exponent = termToExponent.apply(exponentComponentAsExpression.getTerm());
            } else {
                exponent = new ParenthesizedExpression(exponentComponentAsExpression);
            }
        } else {
            throw new RuntimeException("Unexpected type [" + exponentComponent.getClass() + "] for monomial exponent");
        }

        return new Term(coefficient, operator, new Term(new Exponential(base, exponent))); // (a OP b)*(x EXP_OP c)
    }
}