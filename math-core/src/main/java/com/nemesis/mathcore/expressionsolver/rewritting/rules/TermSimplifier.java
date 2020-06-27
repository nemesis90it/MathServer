package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.exception.UnexpectedTermOperatorException;
import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Factor;
import com.nemesis.mathcore.expressionsolver.expression.components.Term;
import com.nemesis.mathcore.expressionsolver.models.Monomial;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.FactorMultiplier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.*;

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
                    case DIVIDE -> Monomial::divide;
                    case MULTIPLY -> Monomial::multiply;
                    default -> throw new UnexpectedTermOperatorException("Unexpected operator [" + term.getOperator() + "]");
                };

                result = monomialOperation.apply(leftMonomial, rightMonomial);
            }

            // If monomials haven't the same base, operator cannot be applied to them (monomialOperation.apply() returns null), then try to multiply factor with rational term

            /*
                (1) -------------------------------------------- TERM --------------------------------------------
                    ------------------ FACTOR ------------------  *   ------------------- SUBTERM ----------------
                                    (leftFactor)                     -------FACTOR------- / -------SUBTERM-------
                                                                         (rightFactor)

                (2) -------------------------------------------- TERM --------------------------------------------
                    ------------------ FACTOR ------------------  *   ------------------- SUBTERM ----------------
                    -------------  getTerm -> TERM -------------      -------FACTOR------  NONE
                                    (leftTerm)                            (rightFactor)
                   -------FACTOR------- / -------SUBTERM-------
                       (leftFactor)

             */

            if (result == null) {
                if (term.getOperator() == MULTIPLY && subTerm.getOperator() == DIVIDE) {
                    final Factor leftFactor = term.getFactor();
                    final Factor rightFactor = subTerm.getFactor();
                    return multiplyFactorWithRationalTerm(subTerm, leftFactor, rightFactor);
                } else if (term.getOperator() == MULTIPLY && subTerm.getOperator() == NONE) {
                    Term leftTerm = Term.getTerm(term.getFactor());
                    if (leftTerm.getOperator() == DIVIDE) {
                        final Factor leftFactor = leftTerm.getFactor();
                        final Factor rightFactor = subTerm.getFactor();
                        return multiplyFactorWithRationalTerm(subTerm, leftFactor, rightFactor);
                    } else {
                        return component;
                    }
                }
            }
            return Objects.requireNonNullElse(result, component);
        };
    }


    private static Term multiplyFactorWithRationalTerm(Term subTerm, Factor leftFactor, Factor rightFactor) {
        Term result;
        Function<Collection<Factor>, ? extends Factor> multiplier;
        if (leftFactor.getClass().equals(rightFactor.getClass())) {
            multiplier = FactorMultiplier.get(leftFactor.getClass());
        } else {
            multiplier = FactorMultiplier.get(Factor.class);
        }

        result = new Term(
                multiplier.apply(Arrays.asList(leftFactor, rightFactor)),
                DIVIDE,
                subTerm.getSubTerm());
        return result;
    }
}
