package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Factor;
import com.nemesis.mathcore.expressionsolver.components.Term;
import com.nemesis.mathcore.expressionsolver.exception.UnexpectedTermOperatorException;
import com.nemesis.mathcore.expressionsolver.monomial.Monomial;
import com.nemesis.mathcore.expressionsolver.monomial.MonomialOperations;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.FactorMultiplier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.*;

public class TermSimplifier implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return Term.class::isInstance;
    }

    @Override
    public Function<Component, ? extends Component> transformer() {
        return component -> {
            Term term = (Term) component;
            Term result = null;
            Term subTerm = term.getSubTerm();
            Factor leftComponent = term.getFactor();
            Component rightComponent = (subTerm != null && subTerm.getOperator() == NONE) ? subTerm.getFactor() : subTerm;

            if (rightComponent == null) {
                return component;
            }

            Monomial leftMonomial = Monomial.getMonomial(leftComponent);
            Monomial rightMonomial = Monomial.getMonomial(rightComponent);

            // If this term can be written as operation between two monomials, apply the operator (MULTIPLY or DIVIDE) to them
            if (rightMonomial != null && leftMonomial != null) {
                BiFunction<Monomial, Monomial, Term> monomialOperation = switch (term.getOperator()) {
                    case DIVIDE -> MonomialOperations::divide;
                    case MULTIPLY -> MonomialOperations::multiply;
                    default -> throw new UnexpectedTermOperatorException("Unexpected operator [" + term.getOperator() + "]");
                };

                result = monomialOperation.apply(leftMonomial, rightMonomial);
            }

            if (result != null) {
                return Objects.requireNonNullElse(result, component);
            }

            // If monomials haven't the same base, operator cannot be applied to them (monomialOperation.apply() returns null), then try to multiply factor with rational term

            /*
                (1) -------------------------------------------- TERM --------------------------------------------
                    ------------------ FACTOR ------------------  *   ------------------- SUBTERM ----------------
                                    (leftFactor)                     -------FACTOR------- / -------SUBTERM-------
                                                                         (rightFactor)          (denominator)

                (2) -------------------------------------------- TERM --------------------------------------------
                    ------------------ FACTOR ------------------  *   ------------------- SUBTERM ----------------
                    -------------  getTerm -> TERM -------------      -------FACTOR------  NONE
                                    (leftTerm)                            (rightFactor)
                   -------FACTOR------- / -------SUBTERM-------
                       (leftFactor)           (denominator)

             */


            if (term.getOperator() == MULTIPLY && subTerm.getOperator() == DIVIDE) { // case (1)
                final Factor leftFactor = term.getFactor();
                final Factor rightFactor = subTerm.getFactor();
                final Term denominator = subTerm.getSubTerm();
                return multiplyFactorWithRationalTerm(leftFactor, rightFactor, denominator);
            }

            if (term.getOperator() == MULTIPLY && subTerm.getOperator() == NONE) { // case (2)
                Term leftTerm = Term.getTerm(term.getFactor());
                if (leftTerm.getOperator() == DIVIDE) {
                    final Factor leftFactor = leftTerm.getFactor();
                    final Factor rightFactor = subTerm.getFactor();
                    final Term denominator = leftTerm.getSubTerm();
                    return multiplyFactorWithRationalTerm(leftFactor, rightFactor, denominator);
                }
            }

            return component;
        };
    }

    /*
        return (leftFactor*rightFactor)/denominator
     */
    private static Term multiplyFactorWithRationalTerm(Factor leftFactor, Factor rightFactor, Term denominator) {

        Function<Collection<Factor>, ? extends Factor> multiplier;
        if (leftFactor.getClass().equals(rightFactor.getClass())) {
            multiplier = FactorMultiplier.get(leftFactor.getClass());
        } else {
            multiplier = FactorMultiplier.get(Factor.class);
        }

        final Factor numerator = multiplier.apply(Arrays.asList(leftFactor, rightFactor));
        SimplifyRationalFunction simplifier = new SimplifyRationalFunction();
        final Component simplifiedComponent = simplifier.applyTo(new Term(numerator, DIVIDE, denominator));
        return Term.getTerm(simplifiedComponent);
    }
}
