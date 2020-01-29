package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Constant;
import com.nemesis.mathcore.expressionsolver.expression.components.Factor;
import com.nemesis.mathcore.expressionsolver.expression.components.Term;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;

public class ZeroTermReduction implements Rule {
    @Override
    public Predicate<Component> precondition() {
        return c -> {
            Term t = Term.getSimplestTerm(c);
            Factor factor = t.getFactor();
            Term subTerm = t.getSubTerm();
            return isZero(factor) || (t.getOperator() == MULTIPLY && isZero(subTerm));

        };
    }

    @Override
    public Function<Component, Constant> transformer() {
        return component -> new Constant("0");
    }


    private static boolean isZero(Component component) {
        return component.isScalar() && component.getValue().compareTo(BigDecimal.ZERO) == 0;
    }
}