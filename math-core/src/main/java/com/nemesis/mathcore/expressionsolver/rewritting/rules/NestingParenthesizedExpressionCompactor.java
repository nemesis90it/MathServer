package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.ParenthesizedExpression;
import com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;

public class NestingParenthesizedExpressionCompactor implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return component -> {
            if (component instanceof ParenthesizedExpression expression) {
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
