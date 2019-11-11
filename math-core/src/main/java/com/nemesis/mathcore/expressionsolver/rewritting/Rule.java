package com.nemesis.mathcore.expressionsolver.rewritting;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.rewritting.rules.DistributiveProperty;
import com.nemesis.mathcore.expressionsolver.rewritting.rules.MonomialTermReduction;
import com.nemesis.mathcore.expressionsolver.rewritting.rules.SignTermSimplifier;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Predicate;


public interface Rule {

    Predicate<Component> condition();

    Function<Component, ? extends Component> transformer();

    default Component tryToApply(Component component) {
        if (this.condition().test(component)) {
            component = this.transformer().apply(component);
        }
        return component;
    }

    public static final Collection<Rule> rules = new LinkedList<>();



}
