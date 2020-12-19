package com.nemesis.mathcore.expressionsolver.utils;


import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.exception.UnexpectedComponentTypeException;
import com.nemesis.mathcore.expressionsolver.models.RationalFunction;
import com.nemesis.mathcore.expressionsolver.monomial.LiteralPart;
import com.nemesis.mathcore.expressionsolver.monomial.Monomial;
import com.nemesis.mathcore.expressionsolver.monomial.MonomialOperations;
import com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.rules.TermSimplifier;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.NONE;
import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.utils.MathCoreContext.Mode.DECIMAL;
import static com.nemesis.mathcore.expressionsolver.utils.MathCoreContext.Mode.FRACTIONAL;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

public class ComponentUtils {

    private ComponentUtils() {
    }

    public static Expression monomialsToExpression(Iterator<Monomial> iterator) {
        if (iterator.hasNext()) {
            Expression expression = new Expression();
            Monomial monomial = iterator.next();
            final LiteralPart literalPart = monomial.getLiteralPart();
            Term term;
            if (!literalPart.isEmpty()) {
                term = new Term(monomial.getCoefficient(), MULTIPLY, Term.buildTerm(literalPart.iterator(), MULTIPLY));
            } else {
                term = new Term(monomial.getCoefficient());
            }
            expression.setTerm(term);
            if (iterator.hasNext()) {
                expression.setOperator(SUM);
                expression.setSubExpression(monomialsToExpression(iterator));
            } else {
                expression.setOperator(NONE);
            }
            return expression;
        }
        return new Expression(new Term(new Constant("0"))); // TODO: return null?
    }

    public static RationalFunction simplifyExponentialSets(Set<Exponential> numeratorExponentialSet, Set<Exponential> denominatorExponentialSet) {

        final LiteralPart clonedNumerator = new LiteralPart(numeratorExponentialSet);
        final LiteralPart clonedDenominator = new LiteralPart(denominatorExponentialSet);

        final Set<Factor> newNumeratorFactors = new TreeSet<>();
        final Set<Factor> newDenominatorFactors = new TreeSet<>();

        for (Iterator<Exponential> numeratorIterator = clonedNumerator.iterator(); numeratorIterator.hasNext(); ) {
            Exponential numeratorFactor = numeratorIterator.next();
            for (Iterator<Exponential> denominatorIterator = clonedDenominator.iterator(); denominatorIterator.hasNext(); ) {
                Exponential denominatorFactor = denominatorIterator.next();
                // The factors in 'denominatorFactor' (and in 'numeratorFactor') will have all different classifier with each others,
                // then the following condition will be true at most one time for each couple of similar factors (ie with same classifier)
                if (Objects.equals(numeratorFactor.classifier(), denominatorFactor.classifier())) {
                    Factor quotient = simplifySimilarExponential(numeratorFactor, denominatorFactor);
                    if (quotient != null) {
                        if (quotient instanceof Exponential exponential && isNegative(exponential.getExponent())) {
                            // Quotient is a negative exponential, then change its exponent sign (make it PLUS) and add to the new denominators set
                            exponential.setExponent(FactorSignInverter.cloneAndChangeSign(exponential.getExponent()));
                            newDenominatorFactors.add(exponential);
                            numeratorIterator.remove(); // Already simplified, disappeared
                            denominatorIterator.remove(); // Already simplified, moved to newDenominatorFactors
                        } else {
                            // Quotient is a factor
                            newNumeratorFactors.add(quotient);
                            numeratorIterator.remove(); // Already simplified, moved to newNumeratorFactors
                            denominatorIterator.remove(); // Already simplified, disappeared
                        }
                    } else { // Current factors are similar (have same classifier) but no simplification can be applied
                        newNumeratorFactors.add(numeratorFactor);
                        newDenominatorFactors.add(denominatorFactor);
                    }
                }
            }
        }

        if (newNumeratorFactors.isEmpty() && newDenominatorFactors.isEmpty()) { // No simplification was possible, returning original elements
            return new RationalFunction(numeratorExponentialSet, denominatorExponentialSet);
        }

        // Add factors that could not be simplified, due are no elements left to attempt simplification
        newNumeratorFactors.addAll(clonedNumerator);
        newDenominatorFactors.addAll(clonedDenominator);

        return new RationalFunction(newNumeratorFactors, newDenominatorFactors);
    }

    private static Factor simplifySimilarExponential(Exponential numerator, Exponential denominator) {

        final Factor numeratorExponent = numerator.getExponent();
        final Factor denominatorExponent = denominator.getExponent();

        if (numeratorExponent.isScalar() && denominatorExponent.isScalar()) {

            Sign newSign = numerator.getSign().equals(denominator.getSign()) ? Sign.PLUS : MINUS;
            BigDecimal newExponent = numeratorExponent.getValue().subtract(denominatorExponent.getValue());

            if (isOne(newExponent)) {
                final Base base = numerator.getBase();
                base.setSign(newSign);
                return base;
            }

            if (isZero(newExponent)) {
                return new Constant(1);
            }

            if (MathCoreContext.getNumericMode() == DECIMAL || (MathCoreContext.getNumericMode() == FRACTIONAL && isInteger(newExponent))) {
                return new Exponential(newSign, numerator.getBase(), new Constant(newExponent));
            }
        }

        return null; // Subtraction between exponents does not simplify result, then numerator and denominator cannot be simplified
    }

    public static Expression getExpression(Component c) {
        if (c instanceof Expression) {
            return (Expression) c;
        } else if (c instanceof Term) {
            return new Expression((Term) c);
        } else if (c instanceof Factor) {
            return new Expression(new Term((Factor) c));
        } else if (c instanceof Monomial) {
            return new Expression(Term.getTerm(c));
        } else {
            throw new UnexpectedComponentTypeException("Unexpected type [" + c.getClass() + "]");
        }
    }

    public static Expression applyConstantToExpression(Expression expr, Constant constant, TermOperator operator) {

        Component term = new TermSimplifier().transformer().apply(new Term(constant, operator, expr.getTerm()));
        Expression expression = new Expression(Term.getTerm(term));

        if (!Objects.equals(expr.getOperator(), NONE)) {
            expression.setOperator(expr.getOperator());
            expression.setSubExpression(applyConstantToExpression(expr.getSubExpression(), constant, operator));
        }

        return expression;
    }

    public static <T extends Constant> Factor applyTermOperator(T a, T b, TermOperator operator) {

        if (operator == TermOperator.NONE) {
            throw new IllegalArgumentException("Cannot apply operator " + TermOperator.NONE.name());
        }

        /* Apply operator to simple constant */

        if (a.getClass().equals(Constant.class) && b.getClass().equals(Constant.class)) {
            if (operator == MULTIPLY) {
                return getProduct(a, b);
            } else {
                return getQuotient(a, b);
            }
        }

        /* Apply operator to constant functions */

        boolean aIsConstantFunction = a.getClass().isAssignableFrom(ConstantFunction.class);
        boolean bIsConstantFunction = b.getClass().isAssignableFrom(ConstantFunction.class);
        if (aIsConstantFunction || bIsConstantFunction) {
            return Factor.getFactor(new Term(Factor.getFactor(a), operator, Factor.getFactor(b)));
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
        return new Fraction(numerator, denominator);

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
        return f instanceof Base b ? b : new ParenthesizedExpression(f);
    }

    public static boolean isZero(Component component) {
        return component != null && component.isScalar() && component.getValue().compareTo(BigDecimal.ZERO) == 0;
    }

    public static boolean isZero(BigDecimal value) {
        return value != null && value.compareTo(ZERO) == 0;
    }


    public static boolean isOne(Component component) {
        return component != null && component.isScalar() && component.getValue().compareTo(ONE) == 0;
    }

    public static boolean isOne(BigDecimal value) {
        return value != null && value.compareTo(ONE) == 0;
    }

    public static boolean isInteger(Component component) {
        return component.isScalar() && MathUtils.isIntegerValue(component.getValue());
    }

    public static boolean isInteger(BigDecimal value) {
        return MathUtils.isIntegerValue(value);
    }

    public static boolean isPositive(Component c) {
        return c.isScalar() && c.getValue().compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isNegative(Component c) {
        return c.isScalar() && c.getValue().compareTo(BigDecimal.ZERO) < 0;
    }

    public static Expression sumSimilarMonomialsAndConvertToExpression(List<Monomial> monomials) {
        final List<Monomial> monomialsSum = sumSimilarMonomials(monomials);
        Expression result;
        if (monomialsSum != monomials) {
            Collections.sort(monomialsSum);
            result = ComponentUtils.monomialsToExpression(monomialsSum.iterator());
        } else {
            Collections.sort(monomials);
            result = ComponentUtils.monomialsToExpression(monomials.iterator());
        }
        return result;
    }

    public static List<Monomial> sumSimilarMonomials(List<Monomial> monomials) {

        List<Monomial> heterogeneousMonomials = new ArrayList<>();
        BinaryOperator<Monomial> monomialAccumulator = (m1, m2) -> Monomial.getMonomial(MonomialOperations.sum(m1, m2));

        Map<LiteralPart, List<Monomial>> similarMonomialsGroups = monomials.stream()
                .collect(Collectors.groupingBy(Monomial::getLiteralPart));

        if (similarMonomialsGroups.values().stream().allMatch(similarMonomials -> similarMonomials.size() == 1)) {
            return monomials; // No similar monomials to sum
        }

        similarMonomialsGroups.forEach((exponentialSet, similarMonomials) -> {
            Monomial sum = similarMonomials.stream().reduce(Monomial.getZero(exponentialSet), monomialAccumulator);
            heterogeneousMonomials.add(sum);
        });

        return heterogeneousMonomials;
    }

    public static boolean isWrappedExpression(Component component, Class<? extends WrappedExpression> type) {
        if (component instanceof Factor f) {
            return (f instanceof ConstantFunction constantFunction && type.isInstance(constantFunction.getComponent())) || type.isInstance(f);
        }
        if (component instanceof Term term) {
            return term.getOperator() == TermOperator.NONE && isWrappedExpression(term.getFactor(), type);
        }
        if (component instanceof Expression expr) {
            return expr.getOperator() == ExpressionOperator.NONE && isWrappedExpression(expr.getTerm(), type);
        }
        throw new IllegalStateException("Unexpected component type: " + component.getClass());
    }
}
