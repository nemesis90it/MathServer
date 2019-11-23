package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.rewritting.Rules;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;

public class ExponentialSimplifier implements Rule {
    @Override
    public Predicate<Component> condition() {
        return Exponential.class::isInstance;
    }

    @Override
    public Function<Component, ? extends Component> transformer() {
        return component -> {

            Exponential exp = (Exponential) component;
            if (exp.getExponent() instanceof Constant) {
                if (exp.getExponent().getValue().equals(BigDecimal.ONE)) {
                    return exp.getBase();
                }
                if (exp.getExponent().getValue().equals(BigDecimal.ZERO)) {
                    return new Constant("1");
                }
            }

            // (a^x)^y = a^(x*y)
            if (exp.getBase() instanceof ParenthesizedExpression) {
                Expression baseAsExpression = ((ParenthesizedExpression) exp.getBase()).getExpression();
                if (baseAsExpression.getOperator() == ExpressionOperator.NONE && baseAsExpression.getTerm().getOperator() == TermOperator.NONE) {
                    Factor factor = baseAsExpression.getTerm().getFactor();
                    if (factor instanceof Exponential) {
                        Exponential factorAsExponential = (Exponential) factor;
                        Component newExponent = new Term(factorAsExponential.getExponent(), MULTIPLY, ComponentUtils.getTerm(exp.getExponent()));
                        for (Rule rule : Rules.rules) {
                            newExponent = newExponent.rewrite(rule);
                        }
                        return new Exponential(factorAsExponential.getBase(), ComponentUtils.getFactor(newExponent));
                    }
                }
            }
            return exp;
        };
    }
}
