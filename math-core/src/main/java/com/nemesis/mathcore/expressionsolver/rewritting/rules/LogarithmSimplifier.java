package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.MULTIPLY;

public class LogarithmSimplifier implements Rule {
    @Override
    public Predicate<Component> precondition() {
        return Logarithm.class::isInstance;
    }

    @Override
    public Function<Component, ? extends Component> transformer() {
        return component -> {

            Logarithm logarithm = (Logarithm) component;

            if (logarithm.getArgument().isScalar()) {

                // base = argument  =>  log(base,arg) = 1
                if (Factor.isFactorOfSubType(logarithm.getArgument(), Constant.class) && logarithm.getArgument().getValue().equals(logarithm.getBase())) {
                    return new Constant("1");
                }

                // log(1) = 0
                if (logarithm.getArgument().getValue().compareTo(BigDecimal.ONE) == 0) {
                    return new Constant("0");
                }
            }

            if (Factor.isFactorOfSubType(logarithm.getArgument(), Exponential.class)) {
                Exponential argument = (Exponential) Factor.getFactor(logarithm.getArgument());
                if (argument.getBase() instanceof Constant && argument.getBase().getValue().equals(logarithm.getBase())) {
                    // log(base, base^x) = x
                    return argument.getExponent();
                } else {
                    // log(x^y) = y*log(x)
                    return new Term(argument.getExponent(), MULTIPLY, new Logarithm(logarithm.getBase(), new ParenthesizedExpression(ComponentUtils.getExpression(argument.getBase()))));
                }
            }

            return logarithm;
        };
    }
}
