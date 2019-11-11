package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.exception.UnexpectedTermOperatorException;
import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Factor;
import com.nemesis.mathcore.expressionsolver.expression.components.Term;
import com.nemesis.mathcore.expressionsolver.models.Monomial;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.NONE;

public class MonomialTermReduction implements Rule {

    @Override
    public Predicate<Component> condition() {
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

            Monomial leftMonomial = Monomial.getMonomial(leftComponent);
            Monomial rightMonomial = Monomial.getMonomial(rightComponent);

            // If this term can be written as operation between two monomials, apply the operator (MULTIPLY or DIVIDE) to them
            if (rightMonomial != null && leftMonomial != null) {
                BiFunction<Monomial, Monomial, Term> monomialOperation;
                switch (term.getOperator()) {
                    case DIVIDE:
                        monomialOperation = Monomial::divide;
                        break;
                    case MULTIPLY:
                        monomialOperation = Monomial::multiply;
                        break;
                    default:
                        throw new UnexpectedTermOperatorException("Unexpected operator [" + term.getOperator() + "]");
                }

                result = monomialOperation.apply(leftMonomial, rightMonomial);
            }

            // If monomials haven't the same base, operator cannot be applied (monomialOperation.apply() returns null)
            return Objects.requireNonNullElse(result, term);
        };
    }
}
