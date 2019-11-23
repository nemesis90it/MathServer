package com.nemesis.mathcore.expressionsolver.rewritting;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Predicate;


public interface Rule {

    Predicate<Component> condition();

    Function<Component, ? extends Component> transformer();

    default Component applyTo(Component component) {
        Predicate<Component> condition = this.condition();
        if (condition.test(component)) {
            component = this.transformer().apply(component);
        }
        return component;
    }

    public static final Collection<Rule> rules = new LinkedList<>();



}
