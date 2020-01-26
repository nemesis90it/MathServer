package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Factor;
import com.nemesis.mathcore.expressionsolver.expression.components.Term;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;

public class OneTermReduction implements Rule {
    @Override
    public Predicate<Component> precondition() {
        return c -> true;
    }

    @Override
    public Function<Component, Component> transformer() {

        return component -> {

            Term t = Term.getSimplestTerm(component);
            Factor factor = t.getFactor();
            Term subTerm = t.getSubTerm();

            if (isOne(factor) && t.getOperator() == MULTIPLY) {
                return Objects.requireNonNullElse(subTerm, t);
            }

            if (subTerm != null && isOne(subTerm)) {
                return factor;
            }

            return component;
        };
    }

    private static boolean isOne(Component component) {
        return component.isScalar() && component.getValue().compareTo(BigDecimal.ONE) == 0;
    }
}
