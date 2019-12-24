package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Constant;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.util.function.Function;
import java.util.function.Predicate;

public class ScalarEvaluator implements Rule {
    @Override
    public Predicate<Component> precondition() {
        return Component::isScalar;
    }

    @Override
    public Function<Component, Constant> transformer() {
        return component -> new Constant(component.getValue().toString());
    }
}
