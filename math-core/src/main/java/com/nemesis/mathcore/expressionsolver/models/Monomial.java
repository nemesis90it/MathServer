package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.NONE;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Monomial extends Component {

    public static final Constant NULL_BASE = new Constant("1");

    private Constant coefficient;
    private Base base;
    private Factor exponent;

    public Constant getCoefficient() {
        return coefficient;
    }

    public Base getBase() {
        return base;
    }

    public Factor getExponent() {
        return exponent;
    }

    public static Monomial getZero(Exponential baseAndExponent) {
        return new Monomial(new Constant("0"), baseAndExponent.getBase(), baseAndExponent.getExponent());
    }

    public static Term sum(Monomial rightMonomial, Monomial leftMonomial) {
        return sumOrSubtract(rightMonomial, leftMonomial, BigDecimal::add);
    }

    public static Term subtract(Monomial rightMonomial, Monomial leftMonomial) {
        return sumOrSubtract(rightMonomial, leftMonomial, BigDecimal::subtract);
    }

    public static Term multiply(Monomial leftMonomial, Monomial rightMonomial) {
        return multiplyOrDivide(leftMonomial, rightMonomial, SUM, MULTIPLY);
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
        return multiplyOrDivide(dividend, divisor, SUBTRACT, TermOperator.DIVIDE);
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
        Sign sign = Sign.PLUS;
        if (component instanceof ParenthesizedExpression) {
            ParenthesizedExpression expression = (ParenthesizedExpression) component;
            if (expression.getOperator().equals(ExpressionOperator.NONE)) {
                leftTerm = expression.getTerm();
                sign = ((ParenthesizedExpression) component).getSign();
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
            if (sign == Sign.MINUS) {
                rightFactor = ComponentUtils.cloneAndChangeSign(rightFactor);
            }
            Factor leftFactor = leftTerm.getFactor();
            if (leftFactor instanceof Constant) {
                return buildMonomial((Constant) leftFactor, rightFactor);
            }
            if (rightFactor instanceof Constant) {
                return buildMonomial((Constant) rightFactor, leftFactor);
            }
        } else if (leftTerm.getOperator().equals(NONE)) {
            Factor factor = leftTerm.getFactor();
            if (sign.equals(Sign.MINUS)) {
                factor = ComponentUtils.cloneAndChangeSign(factor);
            }
            if (factor instanceof Constant) {
                return buildMonomial((Constant) factor, NULL_BASE);
            }
            if (factor instanceof ParenthesizedExpression) {
                return getMonomial(factor);
            }
            Constant constant;
            if (factor.getSign().equals(Sign.MINUS)) {
                factor = ComponentUtils.cloneAndChangeSign(factor);
                constant = new Constant("-1");
            } else {
                constant = new Constant("1");
            }
            return buildMonomial(constant, factor);
        }

        return null;
    }


    private static Monomial buildMonomial(Constant constant, Component component) {

        if (component == null) {
            return new Monomial(constant, null, new Constant("0"));
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
            if (leftMonomial.getBase() == NULL_BASE) {
                return new Term(leftMonomial.getCoefficient());
            } else {
                return new Term(leftMonomial.getCoefficient(), MULTIPLY, new Term(new Exponential(leftMonomial.getBase(), leftMonomial.getExponent())));
            }
        } else if (leftMonomial == null) {
            if (rightMonomial.getBase() == NULL_BASE) {
                return new Term(rightMonomial.getCoefficient());
            } else {
                return new Term(rightMonomial.getCoefficient(), MULTIPLY, new Term(new Exponential(rightMonomial.getBase(), rightMonomial.getExponent())));
            }
        }

        Constant coefficient = new Constant(function.apply(rightMonomial.getCoefficient().getValue(), leftMonomial.getCoefficient().getValue()));

        if (leftMonomial.getBase() == NULL_BASE && rightMonomial.getBase() == NULL_BASE) {
            return new Term(coefficient);
        }

        if (leftMonomial.getBase() == NULL_BASE || rightMonomial.getBase() == NULL_BASE) {
            return null;
        }

        if (!rightMonomial.getBase().absEquals(leftMonomial.getBase()) || !rightMonomial.getExponent().absEquals(leftMonomial.getExponent())) {
            return null;
        }

        Base base = rightMonomial.getBase();
        Factor exponent = rightMonomial.getExponent();

        Monomial m = new Monomial(coefficient, base, exponent);
        return ComponentUtils.getTerm(m);
    }

    /*
        If 'x' is the base:
            a+x^c * b*x^d   =>     (a*b) * x^(c+d)
            a+x^c / b*x^d   =>     (a/b) * x^(c-d)
            a * b*x^d       =>     (a*b) * x^d
            a / b*x^d       =>     (a/b) / x^d
            a*x^c * b       =>     (a*b) * x^c
            a*x^c / b       =>     (a/b) * x^c

     */
    private static Term multiplyOrDivide(Monomial leftMonomial, Monomial rightMonomial, ExpressionOperator exponentOperator, TermOperator operator) {

        BiFunction<BigDecimal, BigDecimal, BigDecimal> function = operator.equals(MULTIPLY) ? BigDecimal::multiply : MathUtils::divide;

        if (leftMonomial == null && rightMonomial == null) {
            return null;
        }

        if (rightMonomial == null) {
            if (leftMonomial.getBase() == NULL_BASE) {
                return new Term(leftMonomial.getCoefficient()); // b
            } else {
                return ComponentUtils.getTerm(new Monomial(leftMonomial.getCoefficient(), leftMonomial.getBase(), leftMonomial.getExponent())); // b OP x^d
            }
        } else if (leftMonomial == null) {
            if (rightMonomial.getBase() == NULL_BASE) {
                return new Term(rightMonomial.getCoefficient()); // a
            } else {
                return ComponentUtils.getTerm(new Monomial(rightMonomial.getCoefficient(), rightMonomial.getBase(), rightMonomial.getExponent())); // a OP x^c
            }
        }

        // Move sign from base to coefficient ('-x' and 'x' have the same base 'x')

        if (leftMonomial.getBase().getSign() == Sign.MINUS) {
            leftMonomial.setBase((Base) ComponentUtils.cloneAndChangeSign(leftMonomial.getBase()));
            leftMonomial.setCoefficient((Constant) ComponentUtils.cloneAndChangeSign(leftMonomial.getCoefficient()));
        }
        if (rightMonomial.getBase().getSign() == Sign.MINUS) {
            rightMonomial.setBase((Base) ComponentUtils.cloneAndChangeSign(rightMonomial.getBase()));
            rightMonomial.setCoefficient((Constant) ComponentUtils.cloneAndChangeSign(rightMonomial.getCoefficient()));
        }

        BigDecimal leftCoefficientValue = leftMonomial.getCoefficient().getValue();
        BigDecimal rightCoefficientValue = rightMonomial.getCoefficient().getValue();
        Constant coefficient = new Constant(function.apply(leftCoefficientValue, rightCoefficientValue));

        if (coefficient.getValue().equals(BigDecimal.ZERO)) {
            return new Term(coefficient); // a OP b
        }

        if (rightMonomial.getBase() == NULL_BASE && leftMonomial.getBase() == NULL_BASE) {
            return new Term(coefficient); // a OP b
        }

        Base base;
        Factor exponent;

        // Result can be a rational function (if operator is DIVIDE)
        if (leftMonomial.getBase() == NULL_BASE) {
            base = rightMonomial.getBase();
            exponent = rightMonomial.getExponent();
            return buildTerm(coefficient, base, exponent, operator);  // (a OP b) OP x^d
        }

        // Result is a monomial
        if (rightMonomial.getBase() == NULL_BASE) {
            base = leftMonomial.getBase();
            exponent = leftMonomial.getExponent();
            return ComponentUtils.getTerm(new Monomial(coefficient, base, exponent)); // (a OP b)*x^c
        }

        if (!rightMonomial.getBase().absEquals(leftMonomial.getBase())) {
            return null;
        }

        base = rightMonomial.getBase(); // Can be used the left monomial, it is the same

        Component exponentComponent = new ParenthesizedExpression(new Term(leftMonomial.getExponent()), exponentOperator, new Expression(new Term(rightMonomial.getExponent()))).simplify();

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

        return new Term(coefficient, MULTIPLY, new Term(new Exponential(base, exponent))); // (a OP b)*(x EXP_OP c)
    }

    private static Term buildTerm(Constant coefficient, Base base, Factor exponent, TermOperator operator) {

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
        return new Term(coefficient, operator, new Term(new Exponential(base, exponent)));
    }

    @Override
    public BigDecimal getValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Component getDerivative() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Component simplify() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(Object o) {
        Comparator<Monomial> comparatorByBase = Comparator.comparing(Monomial::getBase);
        Comparator<Monomial> comparatorByExponent = Comparator.comparing(Monomial::getExponent);
        if (this.getExponent() instanceof Constant && ((Monomial) o).getExponent() instanceof Constant) {
            // Monomials with greater constant degree will be shown from the left, decreasing
            comparatorByExponent = comparatorByExponent.reversed();
        }
        Comparator<Monomial> comparatorByExponential = comparatorByBase.thenComparing(comparatorByExponent);
        Comparator<Monomial> monomialComparator = comparatorByExponential.thenComparing(Monomial::getCoefficient);
        return monomialComparator.compare(this, (Monomial) o);
    }

}