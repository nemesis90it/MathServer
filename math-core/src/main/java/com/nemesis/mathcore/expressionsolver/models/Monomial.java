package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.rewritting.rules.FractionSimplifier;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.expressionsolver.utils.FactorMultiplier;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.DIVIDE;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.utils.ComponentUtils.isOne;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

@Data
@EqualsAndHashCode(callSuper = false)
public class Monomial extends Component {

    private Constant coefficient;
    private LiteralPart literalPart = new LiteralPart();

    public Monomial(Constant coefficient, LiteralPart literalPart) {
        this.coefficient = coefficient;
        this.literalPart = literalPart.getClone();
        moveMinusSignFromBasesToCoefficient(this);
    }


    public Constant getCoefficient() {
        return coefficient;
    }

    public static Monomial getZero(LiteralPart exponentials) {
        return new Monomial(new Constant(0), exponentials);
    }

    public static Term sum(Monomial rightMonomial, Monomial leftMonomial) {
        return applyExpressionOperator(rightMonomial, leftMonomial, SUM);
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


    /* Monomial Tree
     (1)     -----------------------------------EXPRESSION-----------------------------------
     (2)     ----------------------------TERM-------------------------------   NONE     null
     (3)     --LEFT_FACT--  OP   --------------RIGHT_TERM---------------
     (4)                         --RIGHT_FACT--   OP   ---LEFT_TERM---
     (5)         CONST      *         FACT       NONE      null
     (6)         CONST      *         EXPON      NONE      null
     (7)         FACT       *         CONST      NONE      null
     (8)         EXPON      *         CONST      NONE      null
     (5.1)       CONST      *         FACT        *    [see line (4)]
     (6.1)       CONST      *         EXPON       *    [see line (4)]
     (7.1)       FACT       *         CONST       *    [see line (4)]
     (8.1)       EXPON      *         CONST       *    [see line (4)]
     (9)         FACT      NONE                  null
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
                return getMonomial(expression.getTerm());
            } else {
                return null;
            }
        } else if (component instanceof Factor) {
            term = new Term((Factor) component); // see line (3) with OP = NONE, then see line (9)
        } else if (component instanceof Term t) {
            if (t.getOperator() == TermOperator.NONE) {
                // Call this method again so that will be executed one of the other cases (see above)
                return getMonomial(t.getFactor());
            } else {
                term = t;
            }
        } else {
            throw new IllegalArgumentException("Unexpected type [" + component.getClass() + "]");
        }

        if (term.getOperator() == DIVIDE && !term.getSubTerm().isScalar()) {
            return null; // rational monomials aren't considered as monomials
        }

        final Set<Factor> originalFactors = getFactors(term);
        if (originalFactors.isEmpty()) {
            return null;
        }
        Set<Factor> factors = Factor.multiplyFactors(originalFactors);

        final List<Factor> constants = factors.stream()
                .filter(Component::isScalar)
                .collect(Collectors.toList());

        Constant constant;
        if (!constants.isEmpty()) {
            constant = FactorMultiplier.get(Factor.class).apply(constants).getValueAsConstant();
            factors.removeAll(constants);
        } else {
            constant = new Constant(1);
        }

        final TreeSet<Exponential> exponentials = factors.stream().map(Exponential::getExponential).collect(Collectors.toCollection(TreeSet::new));

        return new Monomial(constant, new LiteralPart(exponentials));
    }

    private static Set<Factor> getFactors(Term term) {

        if (term == null) {
            return new TreeSet<>();
        }

        Set<Factor> factors = new TreeSet<>();

        // Add LEFT_FACT and RIGHT_FACT. See lines (5), (6), (7), (8), (5.1), (6.1), (7.1), (8.1)
        factors.add(term.getFactor());

        Term rightTerm = term.getSubTerm();  // see line (4))

        if (rightTerm != null) {
            final Set<Factor> otherFactors;

            switch (term.getOperator()) {
                case MULTIPLY:  // see lines (5.1), (6.1), (7.1), (8.1)
                    otherFactors = getFactors(rightTerm);
                    if (!otherFactors.isEmpty()) {
                        factors.addAll(otherFactors);
                        return factors;
                    } else {
                        return new TreeSet<>();
                    }
                case DIVIDE:
                    otherFactors = getFactors(rightTerm).stream()
                            .map(factor -> new ParenthesizedExpression(new Term(new Constant(ONE), DIVIDE, factor)))
                            .collect(Collectors.toSet());
                    factors.addAll(otherFactors);
                case NONE:
                    factors.add(term.getFactor());  // see line (9)
                    return factors;
                default:
                    throw new IllegalStateException("Unexpected term operator: " + rightTerm.getOperator());
            }
        } else {
            return factors;
        }
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

        Constant coefficient = new Constant(expressionOperator.apply(rightMonomial.getCoefficient().getValue(), leftMonomial.getCoefficient().getValue()));

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

        // Result is a monomial
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

        } else {    // x^a*y^b / x^c*z^d ==> x^(a-c)*y^b / z^d
            final Pair<Set<? extends Factor>, Set<? extends Factor>> simplificationResult = ComponentUtils.simplifyExponentialSets(leftMonomial.getLiteralPart(), rightMonomial.getLiteralPart());

            final Set<? extends Factor> newNumeratorFactors = simplificationResult.getLeft();
            final Set<? extends Factor> newDenominatorFactors = simplificationResult.getRight();

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

    @Override
    public BigDecimal getValue() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Component getDerivative(Variable var) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Component rewrite(Rule rule) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Boolean isScalar() {
        return this.coefficient.isScalar() && this.literalPart.stream().allMatch(Exponential::isScalar);
    }

    @Override
    public Constant getValueAsConstant() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean contains(Variable variable) {
        return literalPart.stream()
                .anyMatch(exponential -> exponential.contains(variable));
    }

    @Override
    public Component getClone() {
        return new Monomial(coefficient.getClone(), this.literalPart.getClone());
    }

    @Override
    public Domain getDomain(Variable variable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Variable> getVariables() {
        throw new UnsupportedOperationException();
    }


    /*
       Comparing each n-th element of the two literalPart (this and other), then compare the two coefficients.

       Looping over the elements:
        - if the two n-th elements are not equals, they difference determines the first difference of the two sets
        - if the two n-th elements are the same, continue the loop to compare the next elements

       Examples:
            [x^2, y^5, z^3] is considered before of [x^2, y^4, z]
                1) x^2 = x^2, continue loop
                2) y^5 < y^4
                    2.1)  5 > 4, then reverse (see exponential comparator), then the comparison of the two sets inherit the comparison result between these two elements
            [x^2, y^5] is considered before of [x^2, z^6]
                1) x^2 = x^2, continue loop
                2) y^5 > z^6 (because y > z), then the comparison of the two sets inherit the comparison result between these two elements
            [x^2, y^5] is considered before of [x^2]
                1) x^2 = x^2, continue loop
                2) No more elements on second set, then the comparison of the two sets inherit the comparison result between y^5 and null (y^5 > null)
    */
    @Override
    public int compareTo(Component c) {
        if (c instanceof Monomial m) {

            Comparator<Monomial> literalPartComparator = (m1, m2) -> {

                Iterator<Exponential> thisIterator = m1.getLiteralPart().iterator();
                Iterator<Exponential> otherIterator = m2.getLiteralPart().iterator();

                while (thisIterator.hasNext()) {
                    Exponential thisExponential = thisIterator.next();
                    if (otherIterator.hasNext()) {
                        Exponential otherExponential = otherIterator.next();
                        final int currentComparison = thisExponential.compareTo(otherExponential);
                        if (currentComparison != 0) {
                            return currentComparison;
                        }
                    } else {
                        return -1; // thisExponential > null, then m1 > m2, then reverse
                    }
                }

                if (otherIterator.hasNext()) {
                    return 1; // null < otherExponential, then m1 < m2, then reverse
                } else {
                    return 0; // All elements are teh same, then m1 = m2
                }
            };

            Comparator<Monomial> monomialComparator = literalPartComparator.thenComparing(Monomial::getCoefficient);
            return monomialComparator.compare(this, m);
        } else {
            throw new UnsupportedOperationException("Comparison between [" + this.getClass() + "] and [" + c.getClass() + "] is not supported yet");
        }
    }


    // Move sign from base to coefficient (actually, '-x' and 'x' have the same base 'x')
    private static void moveMinusSignFromBasesToCoefficient(Monomial monomial) {

        final Set<Exponential> negativeExponentials = new TreeSet<>();

        for (Exponential exponential : monomial.getLiteralPart()) {

            final boolean isBaseNegative = isOne(exponential.getExponent()) && exponential.getBase().getSign() == MINUS;
            final boolean isExponentialNegative = exponential.getSign() == MINUS;

            if (isBaseNegative) {
                exponential.getBase().setSign(PLUS);
            }
            if (isExponentialNegative) {
                exponential.setSign(PLUS);
            }
            if (isBaseNegative ^ isExponentialNegative) {
                negativeExponentials.add(exponential);
            }
        }

        if (negativeExponentials.size() % 2 != 0) {
            monomial.setCoefficient((Constant) ComponentUtils.cloneAndChangeSign(monomial.getCoefficient()));
        }

    }

    @NoArgsConstructor
    public static class LiteralPart extends TreeSet<Exponential> {
        public LiteralPart(Set<Exponential> numeratorExponentialSet) {
            super(numeratorExponentialSet);
        }

        public LiteralPart getClone() {
            TreeSet<Exponential> clonedSet = this.stream().map(Exponential::getClone).collect(Collectors.toCollection(TreeSet::new));
            return new LiteralPart(clonedSet);
        }
    }
}