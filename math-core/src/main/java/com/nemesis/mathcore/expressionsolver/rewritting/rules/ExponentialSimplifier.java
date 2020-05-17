package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;

public class ExponentialSimplifier implements Rule {
    @Override
    public Predicate<Component> precondition() {
        return Exponential.class::isInstance;
    }

    @Override
    public Function<Component, ? extends Component> transformer() {
        return component -> {

            Exponential exp = (Exponential) component;
            if (exp.getExponent() instanceof Constant) {
                if (exp.getExponent().getValue().compareTo(BigDecimal.ONE) == 0) {
                    return exp.getBase();
                }
                if (exp.getExponent().getValue().compareTo(BigDecimal.ZERO) == 0) {
                    return new Constant("1");
                }
            }

            // (a^x)^y = a^(x*y)
            if (exp.getBase() instanceof ParenthesizedExpression) {
                Expression baseAsExpression = ((ParenthesizedExpression) exp.getBase()).getExpression();
                if (baseAsExpression.getOperator() == ExpressionOperator.NONE && baseAsExpression.getTerm().getOperator() == TermOperator.NONE) {
                    Factor factor = baseAsExpression.getTerm().getFactor();
                    if (factor instanceof Exponential factorAsExponential) {
                        Component newExponent = new Term(factorAsExponential.getExponent(), MULTIPLY, Term.getTerm(exp.getExponent()));
                        return new Exponential(factorAsExponential.getBase(), Factor.getFactor(ExpressionUtils.simplify(newExponent)));
                    }
                }
            }
            return exp;
        };
    }
}
