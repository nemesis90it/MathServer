package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.rewritting.rules.FractionSimplifier;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;
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
        if (component instanceof ParenthesizedExpression parExpression) {
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
        } else if (component instanceof Expression expression) {
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
            throw new IllegalArgumentException("Unexpected type [" + component.getClass() + "]");
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

        if (factor instanceof ParenthesizedExpression parExpr) {
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
            throw new IllegalArgumentException("Unexpected operator [" + operator + "]");
        }

        if (factor instanceof Exponential exponential) {
            return new Monomial(constant, exponential.getBase(), toExponentialNotation.apply(exponential.getExponent()));
        } else if (factor instanceof Base) {
            return new Monomial(constant, (Base) factor, toExponentialNotation.apply(new Constant("1")));
        } else {
            throw new IllegalArgumentException("Unexpected type [" + factor.getClass() + "]");
        }
    }

    private static Term applyExpressionOperator(Monomial rightMonomial, Monomial leftMonomial, ExpressionOperator operator) {

        BinaryOperator<BigDecimal> function = operator == SUM ? BigDecimal::add : BigDecimal::subtract;

        if (leftMonomial == null && rightMonomial == null) {
            return null;
        }

        if (rightMonomial == null) {
            if (isNullBase(leftMonomial)) {
                return new Term(leftMonomial.getCoefficient());
            } else {
                return new Term(leftMonomial.getCoefficient(), MULTIPLY, new Exponential(leftMonomial.getBase(), leftMonomial.getExponent()));
            }
        } else if (leftMonomial == null) {
            if (isNullBase(rightMonomial)) {
                return new Term(rightMonomial.getCoefficient());
            } else {
                return new Term(rightMonomial.getCoefficient(), MULTIPLY, new Exponential(rightMonomial.getBase(), rightMonomial.getExponent()));
            }
        }

        Constant coefficient = new Constant(function.apply(rightMonomial.getCoefficient().getValue(), leftMonomial.getCoefficient().getValue()));

        if (isNullBase(leftMonomial) && isNullBase(rightMonomial)) {
            return new Term(coefficient);
        }

        if (isNullBase(leftMonomial) || isNullBase(rightMonomial)) {
            return null;
        }

        if (!rightMonomial.getBase().equals(leftMonomial.getBase()) || !rightMonomial.getExponent().equals(leftMonomial.getExponent())) {
            return null;
        }

        Base base = rightMonomial.getBase();
        Factor exponent = rightMonomial.getExponent();

        return buildTerm(new Term(coefficient), base, exponent);
    }

    /*
        If 'x' is the base:
            a+x^c * b*x^d   =>     (a*b) * x^(c+d)
            a+x^c / b*x^d   =>     (a/b) * x^(c-d)
            a * b*x^d       =>     (a*b) * x^d
            a / b*x^d       =>     (a/b) / x^d     =>   a/b * 1/x^d
            a*x^c * b       =>     (a*b) * x^c
            a*x^c / b       =>     (a/b) * x^c

     */
    private static Term applyTermOperator(Monomial leftMonomial, Monomial rightMonomial, ExpressionOperator exponentOperator, TermOperator operator) {

        if (leftMonomial == null && rightMonomial == null) {
            return null;
        }

        BinaryOperator<BigDecimal> function = operator.equals(MULTIPLY) ? BigDecimal::multiply : MathUtils::divide;

        if (rightMonomial == null) {
            if (isNullBase(leftMonomial)) {
                return new Term(leftMonomial.getCoefficient()); // b
            } else {
                return Term.getSimplestTerm(new Monomial(leftMonomial.getCoefficient(), leftMonomial.getBase(), leftMonomial.getExponent())); // b OP x^d
            }
        } else if (leftMonomial == null) {
            if (isNullBase(rightMonomial)) {
                return new Term(rightMonomial.getCoefficient()); // a
            } else {
                return Term.getSimplestTerm(new Monomial(rightMonomial.getCoefficient(), rightMonomial.getBase(), rightMonomial.getExponent())); // a OP x^c
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

        Constant leftMonomialCoefficient = leftMonomial.getCoefficient();
        Constant rightMonomialCoefficient = rightMonomial.getCoefficient();

        Term coefficient;

        if (MathCoreContext.getNumericMode() == MathCoreContext.Mode.FRACTIONAL) {
            coefficient = ComponentUtils.applyTermOperator(leftMonomialCoefficient, rightMonomialCoefficient, operator);
        } else {
            coefficient = new Term(new Constant(function.apply(leftMonomialCoefficient.getValue(), rightMonomialCoefficient.getValue())));
        }

        if (coefficient.getValue().compareTo(BigDecimal.ZERO) == 0) {
            return new Term(new Constant("0"));
        }

        if (isNullBase(rightMonomial) && isNullBase(leftMonomial)) {
            return coefficient; // a OP b
        }

        Base base;
        Factor exponent;

        // If operator is DIVIDE, result can be a rational function
        if (isNullBase(leftMonomial)) {
            base = rightMonomial.getBase();
            exponent = rightMonomial.getExponent();
            if (operator == DIVIDE) {    //  a / b*x^d  =>  (a/b) / x^d  =>  a/b * 1/x^d
                return buildRationalTerm(coefficient, base, exponent);
            } else {    // a * b*x^d  =>  (a*b) * x^d
                return buildTerm(coefficient, base, exponent);
            }
        }

        // Result is a monomial
        if (isNullBase(rightMonomial)) {
            base = leftMonomial.getBase();
            exponent = leftMonomial.getExponent();
            return buildTerm(coefficient, base, exponent); // (a OP b)*x^c
        }

        if (!rightMonomial.getBase().equals(leftMonomial.getBase())) {
            return null;
        }

        base = rightMonomial.getBase(); // Can be used the left monomial, it is the same

        Component exponentComponent = new ParenthesizedExpression(new Term(leftMonomial.getExponent()), exponentOperator, new Term(rightMonomial.getExponent()));
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
        } else if (exponentComponent instanceof Expression exponentComponentAsExpression) {
            if (exponentComponentAsExpression.getOperator().equals(ExpressionOperator.NONE)) {
                exponent = termToExponent.apply(exponentComponentAsExpression.getTerm());
            } else {
                exponent = new ParenthesizedExpression(exponentComponentAsExpression);
            }
        } else {
            throw new IllegalArgumentException("Unexpected type [" + exponentComponent.getClass() + "] for monomial exponent");
        }

        Exponential exponential = new Exponential(base, exponent);
        Factor factor = Factor.getFactor(ExpressionUtils.simplify(exponential));
        return new Term(coefficient, MULTIPLY, factor); // (a OP b)*(x EXP_OP c)
    }

    public static Term buildTerm(Monomial monomial) {
        return buildTerm(new Term(monomial.getCoefficient()), monomial.getBase(), monomial.getExponent());
    }

    public static Term buildTerm(Term coefficient, Base base, Factor exponent) {

        boolean isExponentNegative = exponent.getSign() == MINUS
                || (exponent.isScalar() && exponent.getValue().compareTo(BigDecimal.ZERO) < 0);

        if (isExponentNegative) { // a(x^-b) = a/x^b
            exponent = ComponentUtils.cloneAndChangeSign(exponent);
            return new Term(coefficient, DIVIDE, new Exponential(base, exponent));
        } else {
            return new Term(coefficient, MULTIPLY, new Exponential(base, exponent));
        }
    }

    public static Term buildRationalTerm(Term coefficient, Base base, Factor exponent) {

        boolean isExponentNegative = exponent.getSign() == MINUS
                || (exponent.isScalar() && exponent.getValue().compareTo(BigDecimal.ZERO) < 0);

        if (isExponentNegative) { // a/(x^-b) = ax^b
            exponent = ComponentUtils.cloneAndChangeSign(exponent);
            return new Term(Factor.getFactor(coefficient), MULTIPLY, new Exponential(base, exponent));
        }

        Fraction f = Factor.getFactorOfSubtype(coefficient, Fraction.class);
        if (f != null) {
            FractionSimplifier fractionSimplifier = new FractionSimplifier();
            if (fractionSimplifier.precondition().test(f)) {
                f = fractionSimplifier.transformer().apply(f);
            }
            Constant numerator = f.getNumerator();
            Constant denominator = f.getDenominator();
            return new Term(numerator,
                    DIVIDE,
                    new ParenthesizedExpression(
                            new Term(denominator, MULTIPLY, new Exponential(base, exponent))
                    )
            );
        }

        return new Term(Factor.getFactor(coefficient), DIVIDE, new Exponential(base, exponent));

    }

    private static boolean isNullBase(Monomial monomial) {
        return Objects.equals(monomial.getBase(), NULL_BASE);
    }

    @Override
    public BigDecimal getValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Component getDerivative(char var) {
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
    public Constant getValueAsConstant() {
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