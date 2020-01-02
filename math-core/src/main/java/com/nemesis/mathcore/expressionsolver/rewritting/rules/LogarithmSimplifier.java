package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.exception.NoValueException;
import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;

public class LogarithmSimplifier implements Rule {
    @Override
    public Predicate<Component> precondition() {
        return Logarithm.class::isInstance;
    }

    @Override
    public Function<Component, ? extends Component> transformer() {
        return component -> {

            Logarithm logarithm = (Logarithm) component;

            // base = argument  =>  log(base,arg) = 1
            if (ComponentUtils.isFactor(logarithm.getArgument(), Constant.class) && logarithm.getArgument().getValue().equals(logarithm.getBase())) {
                return new Constant("1");
            }

            try {
                if (logarithm.getArgument().getValue().compareTo(BigDecimal.ONE) == 0) {
                    return new Constant("0");
                }
            } catch (NoValueException ignored) {
            }

            if (ComponentUtils.isFactor(logarithm.getArgument(), Exponential.class)) {
                Exponential argument = (Exponential) logarithm.getArgument().getTerm().getFactor();
                if (argument.getBase() instanceof Constant && argument.getBase().getValue().equals(logarithm.getBase())) {
                    // log(base, base^x) = x
                    return argument.getExponent();
                } else {
                    // log(x^y) = y*log(x)
                    return new Term(argument.getExponent(), MULTIPLY, new Logarithm(logarithm.getBase(), ComponentUtils.getExpression(argument.getBase())));
                }
            }

            return logarithm;
        };
    }
}
