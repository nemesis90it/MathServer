package com.nemesis.mathcore.expressionsolver.rewritting;

import com.nemesis.mathcore.expressionsolver.rewritting.rules.*;

import java.util.Collection;
import java.util.LinkedList;

public class Rules {

    public static final Collection<Rule> rules = new LinkedList<>();

    static {
        rules.add(new NestingParenthesizedExpressionCompactor());
        rules.add(new ExponentialSimplifier());
        rules.add(new LogarithmSimplifier());
        rules.add(new SignTermSimplifier());
        rules.add(new ApplyMinusSign());
        rules.add(new DistributiveProperty());
        rules.add(new SimilarMonomialsReduction());
        rules.add(new MonomialTermReduction());
    }
}
