package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.ParenthesizedExpression;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;

public class NestingParenthesizedExpressionCompactor implements Rule {

    @Override
    public Predicate<Component> condition() {
        return component -> {
            if (component instanceof ParenthesizedExpression) {
                ParenthesizedExpression expression = (ParenthesizedExpression) component;
                return expression.getOperator() == ExpressionOperator.NONE
                        && expression.getTerm().getOperator() == TermOperator.NONE
                        && expression.getTerm().getFactor() instanceof ParenthesizedExpression;
            }
            return false;
        };
    }

    @Override
    public Function<Component, ? extends Component> transformer() {
        /* Remove useless nesting, moving the inner parenthesized expression on top of the tree */
        return component -> {
            ParenthesizedExpression parExpression = (ParenthesizedExpression) component;
            ParenthesizedExpression innerParExpression = (ParenthesizedExpression) parExpression.getTerm().getFactor();
            parExpression.setSign(!parExpression.getSign().equals(innerParExpression.getSign()) ? MINUS : PLUS);
            parExpression.setExpression(innerParExpression.getExpression());
            return parExpression;
        };
    }
}
