package com.nemesis.mathcore.expressionsolver.rewritting.rules;


import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.models.RationalFunction;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.NONE;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.DIVIDE;
import static java.math.BigDecimal.ONE;

public class SimplifyRationalFunction implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return c ->
                (c instanceof Term term && canApply(term) ||
                        (c instanceof Expression expression) && canApply(expression.getTerm()) ||
                        (c instanceof ParenthesizedExpression parenthesizedExpression) && canApply(parenthesizedExpression.getTerm())
                );
    }

    private static boolean canApply(Term term) {
        return term.getFactor() instanceof ParenthesizedExpression numerator
                && numerator.getOperator() == NONE
                && term.getOperator() == DIVIDE;
    }

    @Override
    public Function<Component, ? extends Component> transformer() {

        return component -> {

            final Term term = Term.getTerm(component);

            final Set<Factor> originalNumeratorFactors = getFactors(((ParenthesizedExpression) term.getFactor()).getTerm());
            final Set<Factor> originalDenominatorFactors = getFactors(term.getSubTerm());

            flattenMultilevelFraction(originalNumeratorFactors, originalDenominatorFactors);

            final Set<Factor> numeratorFactors = Factor.multiplyFactors(originalNumeratorFactors);
            final Set<Factor> denominatorFactors = Factor.multiplyFactors(originalDenominatorFactors);

            final Set<Exponential> numeratorFactorsAsExponential = numeratorFactors.stream().map(Exponential::getExponential).collect(Collectors.toSet());
            final Set<Exponential> denominatorFactorsAsExponential = denominatorFactors.stream().map(Exponential::getExponential).collect(Collectors.toSet());

            final RationalFunction simplificationResult = ComponentUtils.simplifyExponentialSets(numeratorFactorsAsExponential, denominatorFactorsAsExponential);

            final Set<Factor> newNumeratorFactors = simplificationResult.getNumerator();
            final Set<Factor> newDenominatorFactors = simplificationResult.getDenominator();

            if (Objects.equals(newNumeratorFactors, numeratorFactorsAsExponential) && Objects.equals(newDenominatorFactors, denominatorFactorsAsExponential)) {
                return component;    // No simplification was possible
            }

            return Term.buildTerm(newNumeratorFactors, DIVIDE, newDenominatorFactors);
        };
    }

    /*
       See case DIVIDE in method 'getFactors'
    */
    private static void flattenMultilevelFraction(Set<Factor> numerator, Set<Factor> denominator) {

        for (Iterator<Factor> iterator = numerator.iterator(); iterator.hasNext(); ) {
            Factor factor = iterator.next();
            if (factor instanceof ParenthesizedExpression parenthesizedExpression && parenthesizedExpression.getOperator() == NONE) {
                final Term term = parenthesizedExpression.getTerm();
                if (term.getFactor().equals(Constant.ONE) && term.getOperator() == DIVIDE && term.getSubTerm().getOperator() == TermOperator.NONE) {    // (a(1/b))/c -> a/bc
                    iterator.remove();
                    denominator.add(term.getSubTerm().getFactor());
                }
            }
        }
    }


    private static Set<Factor> getFactors(Term term) {

        if (term == null) {
            return new TreeSet<>();
        }

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

}
