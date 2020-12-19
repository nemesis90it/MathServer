package com.nemesis.mathcore.expressionsolver.monomial;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.models.RationalFunction;
import com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.rules.FractionSimplifier;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.DIVIDE;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.MULTIPLY;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

public class MonomialOperations {

    private MonomialOperations() {
    }

    public static Term sum(Monomial rightMonomial, Monomial leftMonomial) {
        final Term term = applyExpressionOperator(rightMonomial, leftMonomial, SUM);
        term.toString();
        return term;
    }

    public static Term subtract(Monomial rightMonomial, Monomial leftMonomial) {
        return applyExpressionOperator(rightMonomial, leftMonomial, SUBTRACT);
    }

    public static Term multiply(Monomial leftMonomial, Monomial rightMonomial) {
        return applyTermOperator(leftMonomial, rightMonomial, MULTIPLY);
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
        return applyTermOperator(dividend, divisor, TermOperator.DIVIDE);
    }

    public static Component power(Monomial base, Constant exponent) {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    private static Term applyExpressionOperator(Monomial rightMonomial, Monomial leftMonomial, ExpressionOperator operator) {

        BinaryOperator<BigDecimal> expressionOperator = operator == SUM ? BigDecimal::add : BigDecimal::subtract;

        if (leftMonomial == null && rightMonomial == null) {
            return null;
        }

        if (rightMonomial == null) {
            return buildTerm(leftMonomial.getCoefficient(), leftMonomial.getLiteralPart());
        } else if (leftMonomial == null) {
            return buildTerm(rightMonomial.getCoefficient(), rightMonomial.getLiteralPart());
        }

        // left and right monomials aren't similar
        if (!Objects.equals(rightMonomial.getLiteralPart(), leftMonomial.getLiteralPart())) {
            return null;
        }

        final Constant rightCoefficient = rightMonomial.getCoefficient();
        final Constant leftCoefficient = leftMonomial.getCoefficient();


        Constant coefficient;
        if (ComponentUtils.isInteger(rightCoefficient) && ComponentUtils.isInteger(leftCoefficient)) {
            coefficient = new Constant(expressionOperator.apply(rightCoefficient.getValue(), leftCoefficient.getValue()));
        } else {
            coefficient = new ParenthesizedExpression(rightCoefficient, operator, leftCoefficient).getValueAsConstant();
        }

        if (hasIdentityLiteralPart(leftMonomial) && hasIdentityLiteralPart(rightMonomial)) {
            return new Term(coefficient);
        }

        // Can be used the exponentialSet of the leftMonomial, it is the same
        return buildTerm(coefficient, rightMonomial.getLiteralPart());

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
    private static Term applyTermOperator(Monomial leftMonomial, Monomial rightMonomial, TermOperator operator) {

        if (leftMonomial == null && rightMonomial == null) {
            return null;
        }

        BinaryOperator<BigDecimal> function = operator.equals(MULTIPLY) ? BigDecimal::multiply : MathUtils::divide;

        if (rightMonomial == null) {
            return buildTerm(leftMonomial.getCoefficient(), leftMonomial.getLiteralPart()); // b OP x^d
        } else if (leftMonomial == null) {
            return buildTerm(rightMonomial.getCoefficient(), rightMonomial.getLiteralPart());   // a OP x^c
        }

        Constant leftMonomialCoefficient = leftMonomial.getCoefficient();
        Constant rightMonomialCoefficient = rightMonomial.getCoefficient();

        Term coefficient;

        if (MathCoreContext.getNumericMode() == MathCoreContext.Mode.FRACTIONAL) {
            coefficient = Term.getTerm(ComponentUtils.applyTermOperator(leftMonomialCoefficient, rightMonomialCoefficient, operator));
        } else {
            coefficient = new Term(new Constant(function.apply(leftMonomialCoefficient.getValue(), rightMonomialCoefficient.getValue())));
        }

        if (coefficient.getValue().compareTo(BigDecimal.ZERO) == 0) {
            return new Term(new Constant(0));
        }

        if (hasIdentityLiteralPart(rightMonomial) && hasIdentityLiteralPart(leftMonomial)) {
            return coefficient; // a OP b
        }

        // If operator is DIVIDE, result can be a rational function
        if (hasIdentityLiteralPart(leftMonomial)) {
            Set<Exponential> literalPart = rightMonomial.getLiteralPart();
            if (operator == DIVIDE) {    //  a / b*x^d  =>  (a/b) / x^d  =>  a/b * 1/x^d
                return buildRationalTerm(coefficient, literalPart);
            } else {    // a * b*x^d  =>  (a*b) * x^d
                return buildTerm(coefficient, literalPart);
            }
        }

        if (hasIdentityLiteralPart(rightMonomial)) {
            Set<Exponential> literalPart = leftMonomial.getLiteralPart();
            return buildTerm(coefficient, literalPart); // (a OP b)*x^c
        }

        if (operator == MULTIPLY) { // x^a*y^b * x^c*z^d  ==>  x^(a+c)*y^b*z^d
            final Map<Base, List<Exponential>> literalPartsByBase = Stream.concat(leftMonomial.getLiteralPart().stream(), rightMonomial.getLiteralPart().stream())
                    .collect(Collectors.groupingBy(Exponential::getBase));

            Set<Exponential> literalParts = literalPartsByBase.entrySet().stream().map(entry -> {
                Factor exponent = entry.getValue().stream().map(Exponential::getExponent)
                        .reduce(new Constant(0), (e1, e2) -> new ParenthesizedExpression(new Term(e1), SUM, new Term(e2)));
                Factor simplifiedExponent = Factor.getFactor(ExpressionUtils.simplify(exponent));
                return new Exponential(entry.getKey(), simplifiedExponent);
            }).collect(Collectors.toCollection(TreeSet::new));

            return buildTerm(coefficient, literalParts);

        } else {    // x^a*y^b / x^c*z^d => x^(a-c)*y^b/z^d
            final RationalFunction simplificationResult = ComponentUtils.simplifyExponentialSets(leftMonomial.getLiteralPart(), rightMonomial.getLiteralPart());

            final Set<Factor> newNumeratorFactors = simplificationResult.getNumerator();
            final Set<Factor> newDenominatorFactors = simplificationResult.getDenominator();

            return new Term(coefficient, MULTIPLY, Term.buildTerm(newNumeratorFactors, DIVIDE, newDenominatorFactors));
        }
    }

    public static Term buildTerm(Component coefficient, Collection<Exponential> literalPart) {

        final Map<Sign, List<Exponential>> exponentialBySign = literalPart.stream().collect(Collectors.groupingBy(exponential -> exponential.getExponent().getSign()));

        final List<Exponential> negativeExponentials = exponentialBySign.get(MINUS);
        final List<Exponential> positiveExponentials = exponentialBySign.get(PLUS);

        Term term;

        if (positiveExponentials != null && !positiveExponentials.isEmpty()) {
            term = new Term(coefficient, MULTIPLY, Term.buildTerm(positiveExponentials.iterator(), MULTIPLY));
        } else {
            term = new Term(coefficient);
        }

        if (negativeExponentials != null && !negativeExponentials.isEmpty()) {  // a(y^c)(x^-b) = ay^c/x^b
            negativeExponentials.forEach(exponential -> exponential.setSign(PLUS));
            term = new Term(term, DIVIDE, Term.buildTerm(negativeExponentials.iterator(), MULTIPLY));
        }

        return term;
    }

    public static Term buildRationalTerm(Term coefficient, Set<Exponential> literalPart) {

        final Map<Sign, List<Exponential>> exponentialBySign = literalPart.stream().collect(Collectors.groupingBy(exponential -> exponential.getExponent().getSign()));

        final List<Exponential> negativeExponentials = exponentialBySign.get(MINUS);
        final List<Exponential> positiveExponentials = exponentialBySign.get(PLUS);

        Term term;

        if (negativeExponentials != null && !negativeExponentials.isEmpty()) { // a/(x^-b) = ax^b
            negativeExponentials.forEach(exponential -> exponential.setSign(PLUS));
            term = new Term(coefficient, MULTIPLY, Term.buildTerm(negativeExponentials.iterator(), MULTIPLY));
        } else {
            Fraction fraction = Factor.getFactorOfSubtype(coefficient, Fraction.class);
            if (fraction != null) { // Let "a/b" the coefficient and [x^c, y^d] the literal part -->  a/b/(x^c*y^d) = a/(b*x^c*y^d)
                FractionSimplifier fractionSimplifier = new FractionSimplifier();
                if (fractionSimplifier.precondition().test(fraction)) {
                    fraction = fractionSimplifier.transformer().apply(fraction);
                }
                Constant numerator = fraction.getNumerator();
                Constant denominator = fraction.getDenominator();
                term = new Term(numerator);
                positiveExponentials.add(0, Exponential.getExponential(denominator));
            } else if (coefficient instanceof Term t && t.getOperator() == DIVIDE) {
                Constant numerator = (Constant) t.getFactor();
                Constant denominator = t.getSubTerm().getFactor().getValueAsConstant();
                term = new Term(numerator);
                positiveExponentials.add(0, Exponential.getExponential(denominator));
            } else {
                term = new Term(coefficient);
            }
        }

        if (positiveExponentials != null && !positiveExponentials.isEmpty()) {
            term = new Term(term, DIVIDE, Term.buildTerm(positiveExponentials.iterator(), MULTIPLY));
        }

        return term;
    }

    private static boolean hasIdentityLiteralPart(Monomial monomial) {
        return monomial.getLiteralPart().stream()
                .allMatch(exponential -> exponential.isScalar() &&
                        exponential.getBase().getValue().compareTo(ZERO) >= 0 &&
                        exponential.getValue().compareTo(ONE) == 0
                );
    }

}
