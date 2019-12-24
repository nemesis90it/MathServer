package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Constant;
import com.nemesis.mathcore.expressionsolver.expression.components.Factor;
import com.nemesis.mathcore.expressionsolver.expression.components.Term;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Predicate;

public class ZeroTermReduction implements Rule {
    @Override
    public Predicate<Component> precondition() {
        return c -> {
            if (c instanceof Term) {
                Term t = (Term) c;
                Factor factor = t.getFactor();
                Term subTerm = t.getSubTerm();
                return (factor.isScalar() && factor.getValue().compareTo(BigDecimal.ZERO) == 0) ||
                        (subTerm != null && subTerm.isScalar() && subTerm.getValue().compareTo(BigDecimal.ZERO) == 0);
            }
            return false;
        };
    }

    @Override
    public Function<Component, Constant> transformer() {
        return component -> new Constant("0");
    }
}
