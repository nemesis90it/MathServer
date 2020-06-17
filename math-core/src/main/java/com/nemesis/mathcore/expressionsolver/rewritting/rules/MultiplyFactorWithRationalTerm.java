package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Term;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.DIVIDE;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;

public class MultiplyFactorWithRationalTerm implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return component ->
                component instanceof Term term
                        && term.getOperator() == MULTIPLY
                        && term.getSubTerm().getOperator() == DIVIDE;
    }

    @Override
    public Function<Component, ? extends Component> transformer() {
        return component -> {
            Term term = (Term) component;
            final Term result = new Term(
                    new Term(term.getFactor(), MULTIPLY, term.getSubTerm().getFactor()),
                    DIVIDE,
                    term.getSubTerm().getSubTerm());
            return result;
        };
    }
}
