package com.nemesis.mathcore.expressionsolver.rewritting.rules;


import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Expression;
import com.nemesis.mathcore.expressionsolver.components.Term;
import com.nemesis.mathcore.expressionsolver.exception.IncompatibleMonomialsException;
import com.nemesis.mathcore.expressionsolver.models.Polynomial;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;


public class PolynomialMultiplication implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return Term.class::isInstance;
    }

    @Override
    public Function<Component, ? extends Component> transformer() {
        return component -> {

            Term term = (Term) component;

            final Pair<LinkedList<Polynomial>, Term> polynomialsAndSubTerm = this.toPolynomials(term);

            // Component does not contain only polynomial (except optional subTerm)
            if (polynomialsAndSubTerm == null) {
                return component;
            }

            final List<Polynomial> polynomials = polynomialsAndSubTerm.getLeft();

            // No polynomials to multiply
            if (polynomials.size() <= 1) {
                return component;
            }

            final Term subTerm = polynomialsAndSubTerm.getRight();

            final Polynomial result;

            try {
                result = polynomials.stream().reduce(Polynomial.IDENTITY_ELEMENT, Polynomial::multiply);
            } catch (IncompatibleMonomialsException e) {
                return component;
            }

            final Expression product = ComponentUtils.monomialsToExpression(result.getMonomials().iterator());

            if (subTerm == null) {
                return product;
            } else {
                return new Term(product, TermOperator.DIVIDE, subTerm);
            }
        };
    }

    private Pair<LinkedList<Polynomial>, Term> toPolynomials(Term term) {
        final Term subTerm = term.getSubTerm();
        Pair<LinkedList<Polynomial>, Term> factorsAndSubTerm = switch (term.getOperator()) {
            case MULTIPLY -> this.toPolynomials(subTerm);
            case NONE -> Pair.of(new LinkedList<>(), null);
            case DIVIDE -> Pair.of(new LinkedList<>(), subTerm);
        };
        if (factorsAndSubTerm == null) {
            return null;
        }
        final Polynomial polynomial = Polynomial.getPolynomial(term.getFactor());
        if (polynomial == null) {
            return null;
        }
        factorsAndSubTerm.getLeft().addFirst(polynomial);
        return factorsAndSubTerm;
    }

}
