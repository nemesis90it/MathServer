package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.*;

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
        return applyExpressionOperator(rightMonomial, leftMonomial, SUM);
    }

    public static Term subtract(Monomial rightMonomial, Monomial leftMonomial) {
        return applyExpressionOperator(rightMonomial, leftMonomial, SUBTRACT);
    }

    public static Term multiply(Monomial leftMonomial, Monomial rightMonomial) {
        return applyTermOperator(leftMonomial, rightMonomial, SUM, MULTIPLY);
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
        return applyTermOperator(dividend, divisor, SUBTRACT, TermOperator.DIVIDE);
    }

    public static Component power(Monomial base, Constant exponent) {
        // TODO
        throw new UnsupportedOperationException();
    }


    /* Monomial Tree
     (1)     -------------------------EXPRESSION-------------------------
     (2)     ------------------TERM---------------------    NONE      null
     (3)     -LEFT_FACT-  OP   --------RIGHT_TERM--------
     (4)                       -RIGHT_FACT-  NONE  null
     (5)     CONST        *      FACT
     (6)     CONST        *      EXPON
     (7)     FACT         *      CONST
     (8)     EXPON        *      CONST
     (9)     FACT        NONE    null
 */
    public static Monomial getMonomial(Component component) {

        Term term;
        if (component instanceof ParenthesizedExpression) {
            ParenthesizedExpression parExpression = (ParenthesizedExpression) component;
            if (parExpression.getOperator().equals(ExpressionOperator.NONE)) { // see line (2), EXPRESSION operation must be NONE
                Expression expression;
                if (parExpression.getSign() == MINUS) {
                    // Remove MINUS sign, changing all signs inside parenthesis
                    expression = ComponentUtils.applyConstantToExpression(parExpression.getExpression(), new Constant("-1"), TermOperator.MULTIPLY);
                } else {
                    expression = parExpression.getExpression();
                }
                // Call this method again so that will be executed the "Expression" case (see below)
                return getMonomial(expression);
            } else {
                return null;
            }
        } else if (component instanceof Expression) {
            Expression expression = (Expression) component;
            if (expression.getOperator().equals(ExpressionOperator.NONE)) {  // see line (2), EXPRESSION operation must be NONE
                term = expression.getTerm();
            } else {
                return null;
            }
        } else if (component instanceof Factor) {
            term = new Term((Factor) component); // see line (3) with OP = NONE, then see line (9)
        } else if (component instanceof Term) {
            term = (Term) component;
        } else {
            throw new RuntimeException("Unexpected type [" + component.getClass() + "]");
        }

        TermOperator termOperator = term.getOperator();

        if (termOperator != NONE) {

            Term rightTerm = term.getSubTerm();
            if (rightTerm.getOperator() != NONE) { // see line (4))
                return null;
            }

            Factor leftFactor = term.getFactor();
            Factor rightFactor = rightTerm.getFactor();
            if (leftFactor instanceof Constant) {
                return buildMonomial((Constant) leftFactor, termOperator, rightFactor); // see lines (5) and (6)
            }
            if (rightFactor instanceof Constant) {
                return buildMonomial((Constant) rightFactor, termOperator, leftFactor); // see lines (7) and (8)
            }
        } else { // see line (9)
            Factor factor = term.getFactor();
            if (factor instanceof ParenthesizedExpression) {
                // Call this method again so that will be executed the "ParenthesizedExpression" case to get the TERM
                return getMonomial(factor);
            }
            if (factor instanceof Constant) {
                // Monomial is a constant
                return buildMonomial((Constant) factor, MULTIPLY, NULL_BASE);
            }
            Constant constant;
            if (factor.getSign().equals(Sign.MINUS)) {
                // Move MINUS sign from factor to a new constant (-1)
                factor = ComponentUtils.cloneAndChangeSign(factor);
                constant = new Constant("-1");
            } else {
                constant = new Constant("1");
            }
            return buildMonomial(constant, MULTIPLY, factor);
        }
        return null;
    }


    private static Monomial buildMonomial(Constant constant, TermOperator operator, Factor factor) {

        if (factor == null) {
            return new Monomial(constant, null, new Constant("0"));
        }

        if (factor instanceof ParenthesizedExpression) {
            ParenthesizedExpression parExpr = (ParenthesizedExpression) factor;
            if (parExpr.getOperator() == ExpressionOperator.NONE) {
                if (parExpr.getTerm().getOperator() == TermOperator.NONE) {
                    return buildMonomial(constant, MULTIPLY, parExpr.getTerm().getFactor());
                }
                return null; // Factor cannot be a term
            }
            /*
            TODO: a*(c+d)^e  where:
                'a' is the coefficient,
                'c+d' is an Expression (as a Base, it is a ParenthesizedExpression),
                'e' is the exponent
             */
            return null;
//            throw new UnsupportedOperationException();
        }

        /*
            If operator is DIVIDE, the result should be a rational function.
            To build it as a monomial, the exponent sign will be changed (and operator become MULTIPLY, implicitly inside Monomial)
         */
        UnaryOperator<Factor> toExponentialNotation;
        if (operator == MULTIPLY) {
            toExponentialNotation = UnaryOperator.identity();
        } else if (operator == DIVIDE) {
            toExponentialNotation = ComponentUtils::cloneAndChangeSign;
        } else {
            throw new RuntimeException("Unexpected operator [" + operator + "]");
        }

        if (factor instanceof Exponential) {
            Exponential exponential = (Exponential) factor;
            return new Monomial(constant, exponential.getBase(), toExponentialNotation.apply(exponential.getExponent()));
        } else if (factor instanceof Base) {
            return new Monomial(constant, (Base) factor, toExponentialNotation.apply(new Constant("1")));
        } else {
            throw new RuntimeException("Unexpected type [" + factor.getClass() + "]");
        }
    }

    private static Term applyExpressionOperator(Monomial rightMonomial, Monomial leftMonomial, ExpressionOperator operator) {

        BinaryOperator<BigDecimal> function = operator == SUM ? BigDecimal::add : BigDecimal::subtract;

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

        if (!rightMonomial.getBase().equals(leftMonomial.getBase()) || !rightMonomial.getExponent().equals(leftMonomial.getExponent())) {
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
    private static Term applyTermOperator(Monomial leftMonomial, Monomial rightMonomial, ExpressionOperator exponentOperator, TermOperator operator) {

        if (leftMonomial == null && rightMonomial == null) {
            return null;
        }

        BinaryOperator<BigDecimal> function = operator.equals(MULTIPLY) ? BigDecimal::multiply : MathUtils::divide;

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

        Constant coefficient;
        if (MathCoreContext.getNumericMode() == MathCoreContext.Mode.FRACTIONAL && operator == DIVIDE) {
            coefficient = new Constant(function.apply(leftCoefficientValue, rightCoefficientValue));
            if (!MathUtils.isIntegerValue(coefficient.getValue())) {
                coefficient = new Fraction(leftCoefficientValue.toBigIntegerExact(), rightCoefficientValue.toBigIntegerExact());
            }
        } else {
            coefficient = new Constant(function.apply(leftCoefficientValue, rightCoefficientValue));
        }

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
            return ComponentUtils.buildTerm(coefficient, base, exponent, operator);  // (a OP b) OP x^d
        }

        // Result is a monomial
        if (rightMonomial.getBase() == NULL_BASE) {
            base = leftMonomial.getBase();
            exponent = leftMonomial.getExponent();
            return ComponentUtils.getTerm(new Monomial(coefficient, base, exponent)); // (a OP b)*x^c
        }

        if (!rightMonomial.getBase().equals(leftMonomial.getBase())) {
            return null;
        }

        base = rightMonomial.getBase(); // Can be used the left monomial, it is the same

        Component exponentComponent = new ParenthesizedExpression(new Term(leftMonomial.getExponent()), exponentOperator, new Expression(new Term(rightMonomial.getExponent())));
        exponentComponent = ExpressionUtils.simplify(exponentComponent);

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

        Exponential exponential = new Exponential(base, exponent);
        Factor factor = ComponentUtils.getFactor(ExpressionUtils.simplify(exponential));
        return new Term(coefficient, MULTIPLY, new Term(factor)); // (a OP b)*(x EXP_OP c)
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
    public Component rewrite(Rule rule) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean isScalar() {
        return null;
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