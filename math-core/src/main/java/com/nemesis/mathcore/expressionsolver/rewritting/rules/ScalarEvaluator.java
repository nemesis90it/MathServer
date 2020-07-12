package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Constant;
import com.nemesis.mathcore.expressionsolver.components.ConstantFunction;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.util.function.Function;
import java.util.function.Predicate;

public class ScalarEvaluator implements Rule {
    @Override
    public Predicate<Component> precondition() {
        return Component::isScalar;
    }

    @Override
    public Function<Component, Component> transformer() {
        return component -> {
            final Constant valueAsConstant = component.getValueAsConstant();
            if (valueAsConstant instanceof ConstantFunction function && function.getComponent() == component) {
                return component; // if no scalar evaluation was possible, function.getComponent() is exactly the same component given as input
            } else {
                return valueAsConstant;
            }
        };
    }
}
