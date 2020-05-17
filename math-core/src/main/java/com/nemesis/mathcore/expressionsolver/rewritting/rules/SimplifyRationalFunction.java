package com.nemesis.mathcore.expressionsolver.rewritting.rules;


import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.expressionsolver.utils.FactorMultiplier;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.*;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

public class SimplifyRationalFunction implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return c ->
                (c instanceof Term term && canApply(term) ||
                        (c instanceof Expression) && canApply(((Expression) c).getTerm()) ||
                        (c instanceof ParenthesizedExpression) && canApply(((ParenthesizedExpression) c).getTerm())
                );
    }

    private static boolean canApply(Term term) {
        return term.getFactor() instanceof ParenthesizedExpression numerator
                && numerator.getOperator() == ExpressionOperator.NONE
                && term.getOperator() == DIVIDE;
    }

    @Override
    public Function<Component, ? extends Component> transformer() {

        return component -> {

            final Term term = Term.getTerm(component);

            final Set<Factor> originalNumeratorFactors = getFactors(((ParenthesizedExpression) term.getFactor()).getTerm());
            final Set<Factor> numeratorFactors = multiplyFactors(originalNumeratorFactors);

            final Set<Factor> originalDenominatorFactors = getFactors(term.getSubTerm());
            final Set<Factor> denominatorFactors = multiplyFactors(originalDenominatorFactors);

            final Function<Factor, Exponential> factorToExponential = f -> f instanceof Base b ? new Exponential(b, new Constant(ONE)) : (Exponential) f;

            final Set<Exponential> numeratorFactorsAsExponential = numeratorFactors.stream().map(factorToExponential).collect(Collectors.toSet());
            final Set<Exponential> denominatorFactorsAsExponential = denominatorFactors.stream().map(factorToExponential).collect(Collectors.toSet());

            final Set<Factor> newNumeratorFactors = new TreeSet<>();
            final Set<Factor> newDenominatorFactors = new TreeSet<>();

            for (Iterator<Exponential> numeratorIterator = numeratorFactorsAsExponential.iterator(); numeratorIterator.hasNext(); ) {
                Exponential numeratorFactor = numeratorIterator.next();
                for (Iterator<Exponential> denominatorIterator = denominatorFactorsAsExponential.iterator(); denominatorIterator.hasNext(); ) {
                    Exponential denominatorFactor = denominatorIterator.next();
                    // The factors in 'denominatorFactor' (and in 'numeratorFactor') will have all different classifier with each others,
                    // then the following condition will be true at most one time for each couple of similar factors (ie with same classifier)
                    if (Objects.equals(numeratorFactor.classifier(), denominatorFactor.classifier())) {
                        Factor quotient = simplifySimilarExponential(numeratorFactor, denominatorFactor);
                        if (quotient != null) {
                            if (quotient instanceof Exponential exp && !isPositive(exp.getExponent())) {
                                // Exponent is negative then change its exponent sign (make it PLUS) and add to the new denominators set
                                exp.setExponent(ComponentUtils.cloneAndChangeSign(exp.getExponent()));
                                newDenominatorFactors.add(exp);
                                numeratorIterator.remove(); // Already simplified, disappeared
                                denominatorIterator.remove(); // Already simplified, moved to newDenominatorFactors
                            } else {
                                newNumeratorFactors.add(quotient);
                                numeratorIterator.remove(); // Already simplified, moved to newNumeratorFactors
                                denominatorIterator.remove(); // Already simplified, disappeared
                            }
                        } else { // Current factors are similar (have same classifier) but no simplification can be applied (for some unknown reason...)
                            newNumeratorFactors.add(numeratorFactor);
                            newDenominatorFactors.add(denominatorFactor);
                        }
                    }
                }
            }

            if (newNumeratorFactors.isEmpty() && newDenominatorFactors.isEmpty()) { // No simplification was possible
                return component;
            }

            // Add factors that could not be simplified, due are no elements left to attempt simplification
            newNumeratorFactors.addAll(numeratorFactorsAsExponential);
            newDenominatorFactors.addAll(denominatorFactorsAsExponential);

            Term simplifiedComponent = new Term(buildTerm(newNumeratorFactors.iterator()));

            if (!newDenominatorFactors.isEmpty()) {
                final Term subTerm = buildTerm(newDenominatorFactors.iterator());
                if (simplifiedComponent.getOperator() == NONE) {
                    simplifiedComponent.setOperator(DIVIDE);
                    simplifiedComponent.setSubTerm(subTerm);
                    return simplifiedComponent;
                } else {
                    return new Term(simplifiedComponent, DIVIDE, subTerm);
                }
            } else {
                return simplifiedComponent;
            }
        };
    }


    private static Term buildTerm(Iterator<Factor> iterator) {
        if (iterator.hasNext()) {
            Term term = Term.getTerm(iterator.next());
            if (iterator.hasNext()) {
                final Term subTerm = buildTerm(iterator);
                if (term.getOperator() == NONE) {
                    term.setOperator(MULTIPLY);
                    term.setSubTerm(subTerm);
                    return term;
                } else {
                    return new Term(term, MULTIPLY, subTerm);
                }
            }
            return term;
        }
        return new Term(new Constant(1));
    }

    private static Factor simplifySimilarExponential(Exponential numerator, Exponential denominator) {

        final Factor numeratorExponent = numerator.getExponent();
        final Factor denominatorExponent = denominator.getExponent();

        if (isInteger(numeratorExponent) && isInteger(denominatorExponent)) {

            Sign newSign = numerator.getSign().equals(denominator.getSign()) ? Sign.PLUS : MINUS;
            BigDecimal newExponent = numeratorExponent.getValue().subtract(denominatorExponent.getValue());

            if (newExponent.compareTo(ONE) == 0) {
                final Base base = numerator.getBase();
                base.setSign(newSign);
                return base;
            }
            if (newExponent.compareTo(ZERO) == 0) {
                return new Constant(1);
            } else {
                return new Exponential(newSign, numerator.getBase(), new Constant(newExponent));
            }

        } else {
            // TODO: support complex exponent subtraction
        }

        return null;

    }

    private static Set<Factor> getFactors(Term term) {

        Set<Factor> factors = new TreeSet<>();
        factors.add(term.getFactor());

        Term subTerm = term.getSubTerm();

        final Set<? extends Factor> subFactors = switch (term.getOperator()) {
            case MULTIPLY -> getFactors(subTerm);
            case DIVIDE -> getFactors(subTerm).stream()
                    .map(factor -> new ParenthesizedExpression(new Term(new Constant(ONE), DIVIDE, factor)))
                    .collect(Collectors.toSet());
            case NONE -> new TreeSet<>();
        };

        factors.addAll(subFactors);

        return factors;
    }

    private Set<Factor> multiplyFactors(Set<Factor> inputFactors) {

        Set<Factor> outputFactors = new TreeSet<>();

        inputFactors.stream()
                .collect(Collectors.groupingBy(Factor::classifier))
                .forEach((classifier, factors) ->
                        outputFactors.add(FactorMultiplier.get(classifier.getFactorClass()).apply(factors))
                );

        return outputFactors;
    }


    private static boolean isInteger(Factor factor) {
        return factor.isScalar() && MathUtils.isIntegerValue(factor.getValue());
    }

    private static boolean isPositive(Factor exp) {
        return exp.getValue().compareTo(BigDecimal.ZERO) > 0;
    }

}
