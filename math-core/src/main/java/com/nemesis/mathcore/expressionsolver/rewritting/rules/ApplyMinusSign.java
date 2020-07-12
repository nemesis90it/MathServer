package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Constant;
import com.nemesis.mathcore.expressionsolver.components.Expression;
import com.nemesis.mathcore.expressionsolver.components.ParenthesizedExpression;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;

public class ApplyMinusSign implements Rule {
    @Override
    public Predicate<Component> precondition() {
        return ParenthesizedExpression.class::isInstance;
    }

    @Override
    public Function<Component, ? extends Component> transformer() {
        return component -> {
            ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) component;
            Expression expression = parenthesizedExpression.getExpression();
            if (parenthesizedExpression.getSign() == MINUS) {
                expression = ComponentUtils.applyConstantToExpression(expression, new Constant("-1"), TermOperator.MULTIPLY);
                parenthesizedExpression.setExpression(expression);
                parenthesizedExpression.setSign(PLUS);
            }
            return parenthesizedExpression;
        };
    }
}
